package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock {

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

    public BlockGoldPowder() {
        super("gold_powder", ModBlocks.prop(Blocks.REDSTONE_WIRE));
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE));
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
        boolean n = state.get(NORTH) != RedstoneSide.NONE;
        boolean e = state.get(EAST) != RedstoneSide.NONE;
        boolean s = state.get(SOUTH) != RedstoneSide.NONE;
        boolean w = state.get(WEST) != RedstoneSide.NONE;

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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST)).with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else {
            return facing == Direction.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST)).with(EAST, this.getSide(worldIn, currentPos, Direction.EAST)).with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH)) : stateIn.with(RedstoneWireBlock.FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
        }
    }

    private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face) {
        BlockPos blockpos = pos.offset(face);
        BlockState blockstate = worldIn.getBlockState(blockpos);
        BlockPos blockpos1 = pos.up();
        BlockState blockstate1 = worldIn.getBlockState(blockpos1);
        if (!blockstate1.isNormalCube(worldIn, blockpos1)) {
            boolean flag = blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
            if (flag && this.canConnectTo(worldIn.getBlockState(blockpos.up()))) {
                if (blockstate.isCollisionShapeOpaque(worldIn, blockpos)) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !this.canConnectTo(blockstate) && (blockstate.isNormalCube(worldIn, blockpos) || !this.canConnectTo(worldIn.getBlockState(blockpos.down()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    protected boolean canConnectTo(BlockState blockState) {
        Block block = blockState.getBlock();
        return block == this;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isSolidSide(worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
            for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
            }
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(direction2);
                if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
                }
            }

        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            if (!worldIn.isRemote) {
                for (Direction direction : Direction.values()) {
                    worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
                }
                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
                }
                for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                    BlockPos blockpos = pos.offset(direction2);
                    if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
                        this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
                    } else {
                        this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
                    }
                }

            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            if (!state.isValidPosition(worldIn, pos)) {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags) {
        try (BlockPos.PooledMutable pool = BlockPos.PooledMutable.retain()) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                RedstoneSide redstoneside = state.get(RedstoneWireBlock.FACING_PROPERTY_MAP.get(direction));
                if (redstoneside != RedstoneSide.NONE && worldIn.getBlockState(pool.setPos(pos).move(direction)).getBlock() != this) {
                    pool.move(Direction.DOWN);
                    BlockState blockstate = worldIn.getBlockState(pool);
                    if (blockstate.getBlock() != Blocks.OBSERVER) {
                        BlockPos blockpos = pool.offset(direction.getOpposite());
                        BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, pool, blockpos);
                        replaceBlock(blockstate, blockstate1, worldIn, pool, flags);
                    }

                    pool.setPos(pos).move(direction).move(Direction.UP);
                    BlockState blockstate3 = worldIn.getBlockState(pool);
                    if (blockstate3.getBlock() != Blocks.OBSERVER) {
                        BlockPos blockpos1 = pool.offset(direction.getOpposite());
                        BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, pool, blockpos1);
                        replaceBlock(blockstate3, blockstate2, worldIn, pool, flags);
                    }
                }
            }
        }

    }

    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() == this) {
            worldIn.notifyNeighborsOfStateChange(pos, this);

            for (Direction direction : Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }
}
