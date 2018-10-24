package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.aura.container.BasicAuraContainer;
import de.ellpeck.naturesaura.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class TileEntityNatureAltar extends TileEntityImpl implements ITickable {

    private static final BlockPos[] BRICK_POSITIONS = new BlockPos[]{
            new BlockPos(-2, -1, 0),
            new BlockPos(-3, -1, 0),
            new BlockPos(-4, 0, 0),
            new BlockPos(-4, 1, 0),

            new BlockPos(2, -1, 0),
            new BlockPos(3, -1, 0),
            new BlockPos(4, 0, 0),
            new BlockPos(4, 1, 0),

            new BlockPos(0, -1, -2),
            new BlockPos(0, -1, -3),
            new BlockPos(0, 0, -4),
            new BlockPos(0, 1, -4),

            new BlockPos(0, -1, 2),
            new BlockPos(0, -1, 3),
            new BlockPos(0, 0, 4),
            new BlockPos(0, 1, 4)
    };
    private static final BlockPos[] MOSSY_POSITIONS = new BlockPos[]{
            new BlockPos(-4, 2, 0),
            new BlockPos(4, 2, 0),
            new BlockPos(0, 2, -4),
            new BlockPos(0, 2, 4),

            new BlockPos(-2, 0, -2),
            new BlockPos(2, 0, -2),
            new BlockPos(2, 0, 2),
            new BlockPos(-2, 0, 2)
    };
    private static final BlockPos[] CHISELED_POSITIONS = new BlockPos[]{
            new BlockPos(1, -1, 1),
            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1),
            new BlockPos(1, -1, -1)
    };
    private static final BlockPos[] WOOD_POSITIONS = new BlockPos[]{
            new BlockPos(-1, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 1),

            new BlockPos(-2, -1, -1),
            new BlockPos(-3, -1, -1),
            new BlockPos(-2, -1, 1),
            new BlockPos(-3, -1, 1),

            new BlockPos(2, -1, -1),
            new BlockPos(3, -1, -1),
            new BlockPos(2, -1, 1),
            new BlockPos(3, -1, 1),

            new BlockPos(-1, -1, -2),
            new BlockPos(-1, -1, -3),
            new BlockPos(1, -1, -2),
            new BlockPos(1, -1, -3),

            new BlockPos(-1, -1, 2),
            new BlockPos(-1, -1, 3),
            new BlockPos(1, -1, 2),
            new BlockPos(1, -1, 3),
    };

    public final ItemStackHandler items = new ItemStackHandlerNA(1, this, true) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected boolean canInsert(ItemStack stack, int slot) {
            return AltarRecipe.forInput(stack) != null || stack.hasCapability(Capabilities.auraContainer, null);
        }

        @Override
        protected boolean canExtract(ItemStack stack, int slot, int amount) {
            return AltarRecipe.forInput(stack) == null;
        }
    };

    private final BasicAuraContainer container = new BasicAuraContainer(5000, true);
    public boolean structureFine;

    private AltarRecipe currentRecipe;
    private int timer;

    private int lastAura;

    @Override
    public void update() {
        Random rand = this.world.rand;

        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 40 == 0) {
                boolean fine = this.check(BRICK_POSITIONS, Blocks.STONEBRICK.getDefaultState(), false)
                        && this.check(MOSSY_POSITIONS, Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.MOSSY), false)
                        && this.check(CHISELED_POSITIONS, Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.CHISELED), false)
                        && this.check(WOOD_POSITIONS, Blocks.PLANKS.getDefaultState(), true);
                if (fine != this.structureFine) {
                    this.structureFine = fine;
                    this.sendToClients();
                }
            }

            if (this.structureFine) {
                int space = this.container.storeAura(3, true);
                if (space > 0) {
                    int toStore = Math.min(AuraChunk.getAuraInArea(this.world, this.pos, 10), space);
                    if (toStore > 0) {
                        BlockPos spot = AuraChunk.getClosestSpot(this.world, this.pos, 10, this.pos);
                        AuraChunk chunk = AuraChunk.getAuraChunk(this.world, spot);

                        chunk.drainAura(spot, toStore);
                        this.container.storeAura(toStore, false);

                        if (this.world.getTotalWorldTime() % 3 == 0)
                            PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticleStream(
                                    this.pos.getX() + (float) rand.nextGaussian() * 10F,
                                    this.pos.getY() + rand.nextFloat() * 10F,
                                    this.pos.getZ() + (float) rand.nextGaussian() * 10F,
                                    this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F,
                                    rand.nextFloat() * 0.05F + 0.05F, 0x89cc37, rand.nextFloat() * 1F + 1F
                            ));
                    }
                }

                ItemStack stack = this.items.getStackInSlot(0);
                if (!stack.isEmpty() && stack.hasCapability(Capabilities.auraContainer, null)) {
                    IAuraContainer container = stack.getCapability(Capabilities.auraContainer, null);
                    int theoreticalDrain = this.container.drainAura(10, true);
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
                            this.currentRecipe = AltarRecipe.forInput(stack);
                        }
                    } else {
                        if (stack.isEmpty() || !stack.isItemEqual(this.currentRecipe.input)) {
                            this.currentRecipe = null;
                            this.timer = 0;
                        } else {
                            int req = this.currentRecipe.aura / this.currentRecipe.time;
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
                        NaturesAura.proxy.spawnMagicParticle(this.world,
                                this.pos.getX() - 4F + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(200) + 100, -0.025F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths) {
                        NaturesAura.proxy.spawnMagicParticle(this.world,
                                this.pos.getX() + 4F + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(200) + 100, -0.025F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 2) {
                        NaturesAura.proxy.spawnMagicParticle(this.world,
                                this.pos.getX() + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() - 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(200) + 100, -0.025F, true, true);
                    }
                    if (this.container.getStoredAura() >= fourths * 3) {
                        NaturesAura.proxy.spawnMagicParticle(this.world,
                                this.pos.getX() + rand.nextFloat(), this.pos.getY() + 3F, this.pos.getZ() + 4F + rand.nextFloat(),
                                0F, 0F, 0F, this.container.getAuraColor(), rand.nextFloat() * 3F + 1F, rand.nextInt(200) + 100, -0.025F, true, true);
                    }

                }
            }
        }
    }

    private boolean check(BlockPos[] positions, IBlockState state, boolean blockOnly) {
        return Helper.checkMultiblock(this.world, this.pos, positions, state, blockOnly);
    }

    @Override
    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeNBT(compound, syncing);
        compound.setTag("items", this.items.serializeNBT());
        compound.setBoolean("fine", this.structureFine);
        this.container.writeNBT(compound);

        if (!syncing) {
            if (this.currentRecipe != null) {
                compound.setString("recipe", this.currentRecipe.name.toString());
                compound.setInteger("timer", this.timer);
            }
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readNBT(compound, syncing);
        this.items.deserializeNBT(compound.getCompoundTag("items"));
        this.structureFine = compound.getBoolean("fine");
        this.container.readNBT(compound);

        if (!syncing) {
            if (compound.hasKey("recipe")) {
                this.currentRecipe = AltarRecipe.RECIPES.get(new ResourceLocation(compound.getString("recipe")));
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
