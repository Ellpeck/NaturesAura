package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.BasicAuraContainer;
import de.ellpeck.naturesaura.aura.IAuraContainer;
import de.ellpeck.naturesaura.aura.IAuraContainerProvider;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityNatureAltar extends TileEntityImpl implements ITickable, IAuraContainerProvider {

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
            new BlockPos(0, -1, 0),
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

    private final List<WeakReference<IAuraContainerProvider>> cachedProviders = new ArrayList<>();
    private final BasicAuraContainer container = new BasicAuraContainer(5000);
    public boolean structureFine;

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
                if (this.world.getTotalWorldTime() % 100 == 0) {
                    this.cachedProviders.clear();
                    for (TileEntity tile : Helper.getTileEntitiesInArea(this.world, this.pos, 15)) {
                        if (tile instanceof IAuraContainerProvider && tile != this) {
                            this.cachedProviders.add(new WeakReference<>((IAuraContainerProvider) tile));
                        }
                    }
                }

                if (!this.cachedProviders.isEmpty()) {
                    IAuraContainerProvider provider = this.cachedProviders.get(rand.nextInt(this.cachedProviders.size())).get();
                    if (provider != null) {
                        int stored = this.container.storeAura(provider.container().drainAura(5, true), false);
                        if (stored > 0) {
                            provider.container().drainAura(stored, false);

                            BlockPos pos = ((TileEntity) provider).getPos();
                            PacketHandler.sendToAllLoaded(this.world, this.pos, new PacketParticles(
                                    pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                                    this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F,
                                    rand.nextFloat() * 0.05F + 0.05F, provider.container().getAuraColor(), rand.nextFloat() * 1F + 1F
                            ));
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
        for (BlockPos offset : positions) {
            IBlockState world = this.world.getBlockState(this.pos.add(offset));
            if (blockOnly ? world.getBlock() != state.getBlock() : world != state) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, boolean syncing) {
        super.writeNBT(compound, syncing);
        compound.setBoolean("fine", this.structureFine);
        this.container.writeNBT(compound);
    }

    @Override
    public void readNBT(NBTTagCompound compound, boolean syncing) {
        super.readNBT(compound, syncing);
        this.structureFine = compound.getBoolean("fine");
        this.container.readNBT(compound);
    }

    @Override
    public IAuraContainer container() {
        return this.container;
    }

    @Override
    public boolean isArtificial() {
        return true;
    }
}
