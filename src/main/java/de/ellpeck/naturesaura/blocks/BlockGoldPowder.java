package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

// TODO
public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock {

    public static final EnumProperty<AttachPos> NORTH = EnumProperty.create("north", AttachPos.class);
    public static final EnumProperty<AttachPos> EAST = EnumProperty.create("east", AttachPos.class);
    public static final EnumProperty<AttachPos> SOUTH = EnumProperty.create("south", AttachPos.class);
    public static final EnumProperty<AttachPos> WEST = EnumProperty.create("west", AttachPos.class);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            VoxelShapes.create(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            VoxelShapes.create(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            VoxelShapes.create(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            VoxelShapes.create(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            VoxelShapes.create(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            VoxelShapes.create(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D),
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)
    };

    public BlockGoldPowder() {
        super("gold_powder", ModBlocks.prop(Blocks.REDSTONE_WIRE));
        this.setDefaultState(this.getDefaultState()
                .with(NORTH, AttachPos.NONE)
                .with(EAST, AttachPos.NONE)
                .with(SOUTH, AttachPos.NONE)
                .with(WEST, AttachPos.NONE));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> 0xf4cb42;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[getShapeIndex(state)];
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPES[getShapeIndex(state.getActualState(source, pos))];
    }

    private static int getShapeIndex(BlockState state) {
        int i = 0;
        boolean n = state.get(NORTH) != AttachPos.NONE;
        boolean e = state.get(EAST) != AttachPos.NONE;
        boolean s = state.get(SOUTH) != AttachPos.NONE;
        boolean w = state.get(WEST) != AttachPos.NONE;

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
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return false;
    }

    public boolean canPlaceBlockAt(IWorldReader worldIn, BlockPos pos) {
        BlockState downState = worldIn.getBlockState(pos.down());
        return downState.isSolid()
                || downState.getBlockFaceShape(worldIn, pos.down(), Direction.UP) == BlockFaceShape.SOLID
                || worldIn.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!world.isRemote()) {
            if (!this.canPlaceBlockAt(world, pos)) {
                this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
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
