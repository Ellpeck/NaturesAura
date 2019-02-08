package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.BasicAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class TileEntityNatureAltar extends TileEntityImpl implements ITickable {

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return TileEntityNatureAltar.this.getRecipeForInput(stack) != null || stack.hasCapability(NaturesAuraAPI.capAuraContainer, null);
        }

        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            if (stack.hasCapability(NaturesAuraAPI.capAuraContainer, null))
                return stack.getCapability(NaturesAuraAPI.capAuraContainer, null).storeAura(1, true) <= 0;
            else
                return TileEntityNatureAltar.this.getRecipeForInput(stack) == null;
        }
    };

    @SideOnly(Side.CLIENT)
    public int bobTimer;

    private final BasicAuraContainer container = new BasicAuraContainer(NaturesAuraAPI.TYPE_OVERWORLD, 500000);
    private final ItemStack[] catalysts = new ItemStack[4];
    public boolean structureFine;

    private AltarRecipe currentRecipe;
    private int timer;

    private int lastAura;

    @Override
    public void update() {
        Random rand = this.world.rand;

        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 40 == 0) {
                boolean fine = Multiblocks.ALTAR.isComplete(this.world, this.pos);
                if (fine != this.structureFine) {
                    this.structureFine = fine;
                    this.sendToClients();
                }

                if (this.structureFine) {
                    int index = 0;
                    for (int x = -2; x <= 2; x += 4) {
                        for (int z = -2; z <= 2; z += 4) {
                            BlockPos offset = this.pos.add(x, 1, z);
                            IBlockState state = this.world.getBlockState(offset);
                            this.catalysts[index] = state.getBlock().getItem(this.world, offset, state);
                            index++;
                        }
                    }
                }
            }

            if (this.structureFine) {
                int space = this.container.storeAura(300, true);
                if (space > 0 && this.container.isAcceptableType(IAuraType.forWorld(this.world))) {
                    int toStore = Math.min(IAuraChunk.getAuraInArea(this.world, this.pos, 20), space);
                    if (toStore > 0) {
                        BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 20, this.pos);
                        IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, spot);

                        chunk.drainAura(spot, toStore);
                        this.container.storeAura(toStore, false);

                        if (this.world.getTotalWorldTime() % 3 == 0)
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                    this.pos.getX() + (float) rand.nextGaussian() * 10F,
                                    this.pos.getY() + rand.nextFloat() * 10F,
                                    this.pos.getZ() + (float) rand.nextGaussian() * 10F,
                                    this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F,
                                    rand.nextFloat() * 0.1F + 0.1F, 0x89cc37, rand.nextFloat() * 1F + 1F
                            ));
                    }
                }

                ItemStack stack = this.items.getStackInSlot(0);
                if (!stack.isEmpty() && stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
                    IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null);
                    int theoreticalDrain = this.container.drainAura(1000, true);
                    if (theoreticalDrain > 0) {
                        int stored = container.storeAura(theoreticalDrain, false);
                        if (stored > 0) {
                            this.container.drainAura(stored, false);

                            if (this.world.getTotalWorldTime() % 4 == 0) {
                                PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 4));
                            }
                        }
                    }
                } else {
                    if (this.currentRecipe == null) {
                        if (!stack.isEmpty()) {
                            this.currentRecipe = this.getRecipeForInput(stack);
                        }
                    } else {
                        if (stack.isEmpty() || !this.currentRecipe.input.apply(stack)) {
                            this.currentRecipe = null;
                            this.timer = 0;
                        } else {
                            int req = MathHelper.ceil(this.currentRecipe.aura / (double) this.currentRecipe.time);
                            if (this.container.getStoredAura() >= req) {
                                this.container.drainAura(req, false);

                                if (this.timer % 4 == 0) {
                                    PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 4));
                                }

                                this.timer++;
                                if (this.timer >= this.currentRecipe.time) {
                                    this.items.setStackInSlot(0, this.currentRecipe.output.copy());
                                    this.currentRecipe = null;
                                    this.timer = 0;

                                    this.world.playSound(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5,
                                            SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS, 0.65F, 1F);
                                }
                            }
                        }
                    }
                }
            }

            if (this.world.getTotalWorldTime() % 10 == 0 && this.lastAura != this.container.getStoredAura()) {
                this.lastAura = this.container.getStoredAura();
                this.sendToClients();
            }
        } else {
            if (this.structureFine) {
                if (rand.nextFloat() >= 0.7F) {
                    int fourths = this.container.getMaxAura() / 4;
                    if (this.container.getStoredAura() > 0) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.pos.getX() - 4F + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.pos.getX() + 4F + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 2) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.pos.getX() + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() - 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 3) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.pos.getX() + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }

                }
            }

            this.bobTimer++;
        }
    }

    private AltarRecipe getRecipeForInput(ItemStack input) {
        for (AltarRecipe recipe : NaturesAuraAPI.ALTAR_RECIPES.values()) {
            if (recipe.input.apply(input)) {
                if (recipe.catalyst == Ingredient.EMPTY)
                    return recipe;
                for (ItemStack stack : this.catalysts)
                    if (recipe.catalyst.apply(stack))
                        return recipe;
            }
        }
        return null;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.setTag("items", this.items.serializeNBT());
            compound.setBoolean("fine", this.structureFine);
            this.container.writeNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (this.currentRecipe != null) {
                compound.setString("recipe", this.currentRecipe.name.toString());
                compound.setInteger("timer", this.timer);
            }
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompoundTag("items"));
            this.structureFine = compound.getBoolean("fine");
            this.container.readNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (compound.hasKey("recipe")) {
                this.currentRecipe = NaturesAuraAPI.ALTAR_RECIPES.get(new ResourceLocation(compound.getString("recipe")));
                this.timer = compound.getInteger("timer");
            }
        }
    }

    @Override
    public IAuraContainer getAuraContainer(EnumFacing facing) {
        return this.container;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(EnumFacing facing) {
        return this.items;
    }
}
