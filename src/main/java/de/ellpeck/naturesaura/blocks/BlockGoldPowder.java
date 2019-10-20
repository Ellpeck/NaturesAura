package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

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
        this.setDefaultState(this.getDefaultState()
                .withProperty(NORTH, AttachPos.NONE)
                .withProperty(EAST, AttachPos.NONE)
                .withProperty(SOUTH, AttachPos.NONE)
                .withProperty(WEST, AttachPos.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return 0;
    }

    @Override
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> 0xf4cb42;
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return AABBS[getAABBIndex(state.getActualState(source, pos))];
    }

    private static int getAABBIndex(BlockState state) {
        int i = 0;
        boolean n = state.getValue(NORTH) != AttachPos.NONE;
        boolean e = state.getValue(EAST) != AttachPos.NONE;
        boolean s = state.getValue(SOUTH) != AttachPos.NONE;
        boolean w = state.getValue(WEST) != AttachPos.NONE;

        if (n || s && !n && !e && !w) {
            i |= 1 << Direction.NORTH.getHorizontalIndex();
        }
        if (e || w && !n && !e && !s) {
            i |= 1 << Direction.EAST.getHorizontalIndex();
        }
        if (s || n && !e && !s && !w) {
            i |= 1 << Direction.SOUTH.getHorizontalIndex();
        }
        if (w || e && !n && !s && !w) {
            i |= 1 << Direction.WEST.getHorizontalIndex();
        }
        return i;
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, Direction.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, Direction.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, Direction.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, Direction.SOUTH));
        return state;
    }

    private AttachPos getAttachPosition(IBlockAccess worldIn, BlockPos pos, Direction direction) {
        BlockPos dirPos = pos.offset(direction);
        BlockState state = worldIn.getBlockState(pos.offset(direction));

        if (!this.canConnectTo(worldIn.getBlockState(dirPos), direction, worldIn, dirPos)
                && (state.isNormalCube() || !this.canConnectUpwardsTo(worldIn, dirPos.down()))) {
            BlockState iblockstate1 = worldIn.getBlockState(pos.up());
            if (!iblockstate1.isNormalCube()) {
                boolean flag = worldIn.getBlockState(dirPos).isSideSolid(worldIn, dirPos, Direction.UP)
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
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockState downState = worldIn.getBlockState(pos.down());
        return downState.isTopSolid()
                || downState.getBlockFaceShape(worldIn, pos.down(), Direction.UP) == BlockFaceShape.SOLID
                || worldIn.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
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

    private boolean canConnectTo(BlockState blockState, @Nullable Direction side, IBlockAccess world, BlockPos pos) {
        Block block = blockState.getBlock();
        return block == this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
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
