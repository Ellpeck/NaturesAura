package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.events.TreeRitualHandler;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock {

    public static final PropertyEnum<AttachPos> NORTH = PropertyEnum.create("north", AttachPos.class);
    public static final PropertyEnum<AttachPos> EAST = PropertyEnum.create("east", AttachPos.class);
    public static final PropertyEnum<AttachPos> SOUTH = PropertyEnum.create("south", AttachPos.class);
    public static final PropertyEnum<AttachPos> WEST = PropertyEnum.create("west", AttachPos.class);
    protected static final AxisAlignedBB[] AABBS = new AxisAlignedBB[]{
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)
    };

    public BlockGoldPowder() {
        super("gold_powder", Material.CIRCUITS);
        this.setSoundType(SoundType.STONE);
        this.setHardness(0F);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextFloat() >= 0.8F) {
            for (BlockPos offsetToOrigin : TreeRitualHandler.GOLD_POWDER_POSITIONS) {
                BlockPos origin = pos.subtract(offsetToOrigin);
                if (Helper.checkMultiblock(worldIn, origin, TreeRitualHandler.GOLD_POWDER_POSITIONS, ModBlocks.GOLD_POWDER.getDefaultState(), true)) {
                    NaturesAura.proxy.spawnMagicParticle(worldIn,
                            pos.getX() + 0.375 + rand.nextFloat() * 0.25, pos.getY() + 0.1, pos.getZ() + 0.375 + rand.nextFloat() * 0.25,
                            rand.nextGaussian() * 0.001, rand.nextFloat() * 0.001 + 0.005, rand.nextGaussian() * 0.001,
                            0xf4cb42, 1F, 50, 0F, false, true);
                    break;
                }
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> 0xf4cb42;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABBS[getAABBIndex(state.getActualState(source, pos))];
    }

    private static int getAABBIndex(IBlockState state) {
        int i = 0;
        boolean n = state.getValue(NORTH) != AttachPos.NONE;
        boolean e = state.getValue(EAST) != AttachPos.NONE;
        boolean s = state.getValue(SOUTH) != AttachPos.NONE;
        boolean w = state.getValue(WEST) != AttachPos.NONE;

        if (n || s && !n && !e && !w) {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }
        if (e || w && !n && !e && !s) {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }
        if (s || n && !e && !s && !w) {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }
        if (w || e && !n && !s && !w) {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }
        return i;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }

    private AttachPos getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
        BlockPos dirPos = pos.offset(direction);
        IBlockState state = worldIn.getBlockState(pos.offset(direction));

        if (!this.canConnectTo(worldIn.getBlockState(dirPos), direction, worldIn, dirPos)
                && (state.isNormalCube() || !this.canConnectUpwardsTo(worldIn, dirPos.down()))) {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.up());
            if (!iblockstate1.isNormalCube()) {
                boolean flag = worldIn.getBlockState(dirPos).isSideSolid(worldIn, dirPos, EnumFacing.UP)
                        || worldIn.getBlockState(dirPos).getBlock() == Blocks.GLOWSTONE;
                if (flag && this.canConnectUpwardsTo(worldIn, dirPos.up())) {
                    if (state.isBlockNormalCube()) {
                        return AttachPos.UP;
                    }
                    return AttachPos.SIDE;
                }
            }
            return AttachPos.NONE;
        } else {
            return AttachPos.SIDE;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.isTopSolid()
                || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID
                || worldIn.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (!this.canPlaceBlockAt(worldIn, pos)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    private boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
        return this.canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
    }

    private boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side, IBlockAccess world, BlockPos pos) {
        Block block = blockState.getBlock();
        return block == this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    private enum AttachPos implements IStringSerializable {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        AttachPos(String name) {
            this.name = name;
        }

        public String toString() {
            return this.getName();
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
