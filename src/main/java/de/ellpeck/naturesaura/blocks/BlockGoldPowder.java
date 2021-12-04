package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.util.math.shapes.Shapes;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevel;
import net.minecraft.level.ILevelReader;
import net.minecraft.level.Level;

import java.util.function.Supplier;

public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

    public BlockGoldPowder() {
        super("gold_powder", Properties.from(Blocks.REDSTONE_WIRE));
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE));
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public IBlockColor getBlockColor() {
        return (state, levelIn, pos, tintIndex) -> 0xf4cb42;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[getShapeIndex(state)];
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getLevel();
        BlockPos blockpos = context.getPos();
        return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST)).with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, ILevel levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else {
            return facing == Direction.UP ? stateIn.with(WEST, this.getSide(levelIn, currentPos, Direction.WEST)).with(EAST, this.getSide(levelIn, currentPos, Direction.EAST)).with(NORTH, this.getSide(levelIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(levelIn, currentPos, Direction.SOUTH)) : stateIn.with(RedstoneWireBlock.FACING_PROPERTY_MAP.get(facing), this.getSide(levelIn, currentPos, facing));
        }
    }

    private RedstoneSide getSide(IBlockReader levelIn, BlockPos pos, Direction face) {
        BlockPos blockpos = pos.offset(face);
        BlockState blockstate = levelIn.getBlockState(blockpos);
        BlockPos blockpos1 = pos.up();
        BlockState blockstate1 = levelIn.getBlockState(blockpos1);
        if (!blockstate1.isNormalCube(levelIn, blockpos1)) {
            boolean flag = blockstate.isSolidSide(levelIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
            if (flag && this.canConnectTo(levelIn.getBlockState(blockpos.up()))) {
                if (blockstate.hasOpaqueCollisionShape(levelIn, blockpos)) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !this.canConnectTo(blockstate) && (blockstate.isNormalCube(levelIn, blockpos) || !this.canConnectTo(levelIn.getBlockState(blockpos.down()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    protected boolean canConnectTo(BlockState blockState) {
        Block block = blockState.getBlock();
        return block == this;
    }

    @Override
    public boolean isValidPosition(BlockState state, ILevelReader levelIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = levelIn.getBlockState(blockpos);
        return blockstate.isSolidSide(levelIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return Shapes.empty();
    }

    @Override
    public void onBlockAdded(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && !levelIn.isClientSide) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                levelIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
            for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(levelIn, pos.offset(direction1));
            }
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(direction2);
                if (levelIn.getBlockState(blockpos).isNormalCube(levelIn, blockpos)) {
                    this.notifyWireNeighborsOfStateChange(levelIn, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(levelIn, blockpos.down());
                }
            }

        }
    }

    @Override
    public void onReplaced(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, levelIn, pos, newState, isMoving);
            if (!levelIn.isClientSide) {
                for (Direction direction : Direction.values()) {
                    levelIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
                }
                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.notifyWireNeighborsOfStateChange(levelIn, pos.offset(direction1));
                }
                for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                    BlockPos blockpos = pos.offset(direction2);
                    if (levelIn.getBlockState(blockpos).isNormalCube(levelIn, blockpos)) {
                        this.notifyWireNeighborsOfStateChange(levelIn, blockpos.up());
                    } else {
                        this.notifyWireNeighborsOfStateChange(levelIn, blockpos.down());
                    }
                }

            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!levelIn.isClientSide) {
            if (!state.isValidPosition(levelIn, pos)) {
                spawnDrops(state, levelIn, pos);
                levelIn.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void updateDiagonalNeighbors(BlockState state, ILevel levelIn, BlockPos pos, int flags, int recursionLeft) {
        BlockPos.Mutable pool = new BlockPos.Mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.get(RedstoneWireBlock.FACING_PROPERTY_MAP.get(direction));
            if (redstoneside != RedstoneSide.NONE && levelIn.getBlockState(pool.setPos(pos).move(direction)).getBlock() != this) {
                pool.move(Direction.DOWN);
                BlockState blockstate = levelIn.getBlockState(pool);
                if (blockstate.getBlock() != Blocks.OBSERVER) {
                    BlockPos blockpos = pool.offset(direction.getOpposite());
                    BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), levelIn.getBlockState(blockpos), levelIn, pool, blockpos);
                    replaceBlock(blockstate, blockstate1, levelIn, pool, flags);
                }

                pool.setPos(pos).move(direction).move(Direction.UP);
                BlockState blockstate3 = levelIn.getBlockState(pool);
                if (blockstate3.getBlock() != Blocks.OBSERVER) {
                    BlockPos blockpos1 = pool.offset(direction.getOpposite());
                    BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), levelIn.getBlockState(blockpos1), levelIn, pool, blockpos1);
                    replaceBlock(blockstate3, blockstate2, levelIn, pool, flags);
                }
            }
        }

    }

    private void notifyWireNeighborsOfStateChange(Level levelIn, BlockPos pos) {
        if (levelIn.getBlockState(pos).getBlock() == this) {
            levelIn.notifyNeighborsOfStateChange(pos, this);

            for (Direction direction : Direction.values()) {
                levelIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        // noop
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "item/" + this.getBaseName());
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
    }
}
