package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock {

    public static final EnumProperty<AttachPos> NORTH = EnumProperty.create("north", AttachPos.class);
    public static final EnumProperty<AttachPos> EAST = EnumProperty.create("east", AttachPos.class);
    public static final EnumProperty<AttachPos> SOUTH = EnumProperty.create("south", AttachPos.class);
    public static final EnumProperty<AttachPos> WEST = EnumProperty.create("west", AttachPos.class);
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

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

    // TODO weird gold powder thing
/*
    @Override
    public BlockState getActualState(BlockState state, IWorld worldIn, BlockPos pos) {
        state = state.with(WEST, this.getAttachPosition(worldIn, pos, Direction.WEST));
        state = state.with(EAST, this.getAttachPosition(worldIn, pos, Direction.EAST));
        state = state.with(NORTH, this.getAttachPosition(worldIn, pos, Direction.NORTH));
        state = state.with(SOUTH, this.getAttachPosition(worldIn, pos, Direction.SOUTH));
        return state;
    }

    private AttachPos getAttachPosition(IWorld worldIn, BlockPos pos, Direction direction) {
        BlockPos dirPos = pos.offset(direction);
        BlockState state = worldIn.getBlockState(pos.offset(direction));

        if (!this.canConnectTo(worldIn.getBlockState(dirPos), direction, worldIn, dirPos)
                && (state.isNormalCube(worldIn, pos.offset(direction)) || !this.canConnectUpwardsTo(worldIn, dirPos.down()))) {
            BlockState iblockstate1 = worldIn.getBlockState(pos.up());
            if (!iblockstate1.isNormalCube(worldIn, pos.up())) {
                *//*boolean flag = worldIn.getBlockState(dirPos).isSideSolid(worldIn, dirPos, Direction.UP)
                        || worldIn.getBlockState(dirPos).getBlock() == Blocks.GLOWSTONE;
                if (flag && this.canConnectUpwardsTo(worldIn, dirPos.up())) {
                    if (state.isBlockNormalCube()) {
                        return AttachPos.UP;
                    }
                    return AttachPos.SIDE;
                }*//*
                return AttachPos.SIDE;
            }
            return AttachPos.NONE;
        } else {
            return AttachPos.SIDE;
        }
    }*/

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.func_224755_d(worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
    }

    private boolean canConnectUpwardsTo(IWorld worldIn, BlockPos pos) {
        return this.canConnectTo(worldIn.getBlockState(pos), null, worldIn, pos);
    }

    private boolean canConnectTo(BlockState blockState, @Nullable Direction side, IWorld world, BlockPos pos) {
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
