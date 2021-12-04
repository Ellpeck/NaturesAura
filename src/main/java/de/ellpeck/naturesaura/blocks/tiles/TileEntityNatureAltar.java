package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.BasicAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class BlockEntityNatureAltar extends BlockEntityImpl implements ITickableBlockEntity {

    private final BasicAuraContainer container = new BasicAuraContainer(null, 500000) {
        @Override
        public int getAuraColor() {
            return IAuraType.forLevel(BlockEntityNatureAltar.this.level).getColor();
        }
    };
    private final ItemStack[] catalysts = new ItemStack[4];
    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return BlockEntityNatureAltar.this.getRecipeForInput(stack) != null || stack.getCapability(NaturesAuraAPI.capAuraContainer, null).isPresent();
        }

        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            IAuraContainer cap = stack.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null);
            if (cap != null)
                return cap.storeAura(1, true) <= 0;
            else
                return BlockEntityNatureAltar.this.getRecipeForInput(stack) == null;
        }
    };
    @OnlyIn(Dist.CLIENT)
    public int bobTimer;
    public StructureState structureState = StructureState.INVALID;

    private AltarRecipe currentRecipe;
    private int timer;

    private int lastAura;
    private boolean firstTick = true;

    public BlockEntityNatureAltar() {
        super(ModTileEntities.NATURE_ALTAR);
    }

    @Override
    public void tick() {
        Random rand = this.level.rand;

        if (this.level.getGameTime() % 40 == 0) {
            int index = 0;
            for (int x = -2; x <= 2; x += 4) {
                for (int z = -2; z <= 2; z += 4) {
                    BlockPos offset = this.worldPosition.add(x, 1, z);
                    BlockState state = this.level.getBlockState(offset);
                    this.catalysts[index] = state.getBlock().getItem(this.level, offset, state);
                    index++;
                }
            }
        }

        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 40 == 0 || this.firstTick) {
                StructureState newState = this.getNewState();
                if (newState != this.structureState) {
                    this.structureState = newState;
                    this.sendToClients();
                }
                this.firstTick = false;
            }

            if (this.structureState != StructureState.INVALID) {
                int space = this.container.storeAura(300, true);
                IAuraType expectedType = this.structureState == StructureState.NETHER ? NaturesAuraAPI.TYPE_NETHER : NaturesAuraAPI.TYPE_OVERWORLD;
                if (space > 0 && IAuraType.forLevel(this.level).isSimilar(expectedType)) {
                    int toStore = Math.min(IAuraChunk.getAuraInArea(this.level, this.worldPosition, 20), space);
                    if (toStore > 0) {
                        BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 20, this.worldPosition);
                        IAuraChunk chunk = IAuraChunk.getAuraChunk(this.level, spot);

                        chunk.drainAura(spot, toStore);
                        this.container.storeAura(toStore, false);

                        if (this.level.getGameTime() % 3 == 0)
                            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticleStream(
                                    this.worldPosition.getX() + (float) rand.nextGaussian() * 10F,
                                    this.worldPosition.getY() + rand.nextFloat() * 10F,
                                    this.worldPosition.getZ() + (float) rand.nextGaussian() * 10F,
                                    this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F,
                                    rand.nextFloat() * 0.1F + 0.1F, this.container.getAuraColor(), rand.nextFloat() * 1F + 1F
                            ));
                    }
                }

                ItemStack stack = this.items.getStackInSlot(0);
                IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null).orElse(null);
                if (!stack.isEmpty() && container != null) {
                    int theoreticalDrain = this.container.drainAura(1000, true);
                    if (theoreticalDrain > 0) {
                        int stored = container.storeAura(theoreticalDrain, false);
                        if (stored > 0) {
                            this.container.drainAura(stored, false);

                            if (this.level.getGameTime() % 4 == 0)
                                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.ALTAR_CONVERSION, this.container.getAuraColor()));
                        }
                    }
                } else {
                    if (this.currentRecipe == null) {
                        if (!stack.isEmpty()) {
                            this.currentRecipe = this.getRecipeForInput(stack);
                        }
                    } else {
                        if (stack.isEmpty() || !this.currentRecipe.input.test(stack)) {
                            this.currentRecipe = null;
                            this.timer = 0;
                        } else {
                            int req = MathHelper.ceil(this.currentRecipe.aura / (double) this.currentRecipe.time);
                            if (this.container.getStoredAura() >= req) {
                                this.container.drainAura(req, false);

                                if (this.timer % 4 == 0)
                                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.ALTAR_CONVERSION, this.container.getAuraColor()));

                                this.timer++;
                                if (this.timer >= this.currentRecipe.time) {
                                    this.items.setStackInSlot(0, this.currentRecipe.output.copy());
                                    this.currentRecipe = null;
                                    this.timer = 0;

                                    this.level.playSound(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5,
                                            SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS, 0.65F, 1F);
                                }
                            }
                        }
                    }
                }
            }

            if (this.level.getGameTime() % 10 == 0 && this.lastAura != this.container.getStoredAura()) {
                this.lastAura = this.container.getStoredAura();
                this.sendToClients();
            }
        } else {
            if (this.structureState != StructureState.INVALID) {
                if (rand.nextFloat() >= 0.7F) {
                    int fourths = this.container.getMaxAura() / 4;
                    if (this.container.getStoredAura() > 0) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() - 4F + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + 4F + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 2) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() - 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 3) {
                        NaturesAuraAPI.instance().spawnMagicParticle(
                                this.worldPosition.getX() + rand.nextFloat(), this.worldPosition.getY() + 3F, this.worldPosition.getZ() + 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(100) + 50, -0.05F, true, true);
                    }

                }
            }

            this.bobTimer++;
        }
    }

    private AltarRecipe getRecipeForInput(ItemStack input) {
        IAuraType type = IAuraType.forLevel(this.level);
        for (AltarRecipe recipe : this.level.getRecipeManager().getRecipes(ModRecipes.ALTAR_TYPE, null, null)) {
            if (recipe.input.test(input) && (recipe.requiredType == null || type.isSimilar(recipe.requiredType))) {
                if (recipe.catalyst == Ingredient.EMPTY)
                    return recipe;
                for (ItemStack stack : this.catalysts)
                    if (recipe.catalyst.test(stack))
                        return recipe;
            }
        }
        return null;
    }

    private StructureState getNewState() {
        if (Multiblocks.ALTAR.isComplete(this.level, this.worldPosition))
            return StructureState.OVERWORLD;
        if (Multiblocks.NETHER_ALTAR.isComplete(this.level, this.worldPosition))
            return StructureState.NETHER;
        return StructureState.INVALID;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.put("items", this.items.serializeNBT());
            compound.putString("state", this.structureState.name());
            this.container.writeNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (this.currentRecipe != null) {
                compound.putString("recipe", this.currentRecipe.name.toString());
                compound.putInt("timer", this.timer);
            }
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.items.deserializeNBT(compound.getCompound("items"));
            if (compound.contains("state"))
                this.structureState = StructureState.valueOf(compound.getString("state"));
            this.container.readNBT(compound);
        }
        if (type == SaveType.TILE) {
            if (compound.contains("recipe")) {
                if (this.hasLevel())
                    this.currentRecipe = (AltarRecipe) this.level.getRecipeManager().getRecipe(new ResourceLocation(compound.getString("recipe"))).orElse(null);
                this.timer = compound.getInt("timer");
            }
        }
    }

    @Override
    public IAuraContainer getAuraContainer() {
        return this.container;
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        return this.items;
    }

    public enum StructureState {
        INVALID,
        NETHER,
        OVERWORLD
    }
}
