package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class BlockGoldPowder extends BlockImpl implements IColorProvidingBlock, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.NORTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.EAST_REDSTONE;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.SOUTH_REDSTONE;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.WEST_REDSTONE;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), box(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), box(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), box(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};

    public BlockGoldPowder() {
        super("gold_powder", Properties.copy(Blocks.REDSTONE_WIRE));
        this.registerDefaultState(this.defaultBlockState().setValue(NORTH, RedstoneSide.NONE).setValue(EAST, RedstoneSide.NONE).setValue(SOUTH, RedstoneSide.NONE).setValue(WEST, RedstoneSide.NONE));
    }

    private static int getShapeIndex(BlockState state) {
        int i = 0;
        boolean n = state.getValue(NORTH) != RedstoneSide.NONE;
        boolean e = state.getValue(EAST) != RedstoneSide.NONE;
        boolean s = state.getValue(SOUTH) != RedstoneSide.NONE;
        boolean w = state.getValue(WEST) != RedstoneSide.NONE;

        if (n || s && !n && !e && !w) {
            i |= 1 << Direction.NORTH.ordinal();
        }
        if (e || w && !n && !e && !s) {
            i |= 1 << Direction.EAST.ordinal();
        }
        if (s || n && !e && !s && !w) {
            i |= 1 << Direction.SOUTH.ordinal();
        }
        if (w || e && !n && !s && !w) {
            i |= 1 << Direction.WEST.ordinal();
        }
        return i;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockColor getBlockColor() {
        return (state, levelIn, pos, tintIndex) -> 0xf4cb42;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return SHAPES[getShapeIndex(state)];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState().setValue(WEST, this.getSide(iblockreader, blockpos, Direction.WEST)).setValue(EAST, this.getSide(iblockreader, blockpos, Direction.EAST)).setValue(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH)).setValue(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else {
            return facing == Direction.UP ? stateIn.setValue(WEST, this.getSide(levelIn, currentPos, Direction.WEST)).setValue(EAST, this.getSide(levelIn, currentPos, Direction.EAST)).setValue(NORTH, this.getSide(levelIn, currentPos, Direction.NORTH)).setValue(SOUTH, this.getSide(levelIn, currentPos, Direction.SOUTH)) : stateIn.setValue(RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(facing), this.getSide(levelIn, currentPos, facing));
        }
    }

    private RedstoneSide getSide(BlockGetter levelIn, BlockPos pos, Direction face) {
        BlockPos blockpos = pos.relative(face);
        BlockState blockstate = levelIn.getBlockState(blockpos);
        BlockPos blockpos1 = pos.above();
        BlockState blockstate1 = levelIn.getBlockState(blockpos1);
        if (!blockstate1.isCollisionShapeFullBlock(levelIn, blockpos1)) {
            boolean flag = blockstate.isFaceSturdy(levelIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
            if (flag && this.canConnectTo(levelIn.getBlockState(blockpos.above()))) {
                if (blockstate.isCollisionShapeFullBlock(levelIn, blockpos)) {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !this.canConnectTo(blockstate) && (blockstate.isCollisionShapeFullBlock(levelIn, blockpos) || !this.canConnectTo(levelIn.getBlockState(blockpos.below()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    protected boolean canConnectTo(BlockState blockState) {
        Block block = blockState.getBlock();
        return block == this;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = levelIn.getBlockState(blockpos);
        return blockstate.isFaceSturdy(levelIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && !levelIn.isClientSide) {
            for (Direction direction : Direction.Plane.VERTICAL) {
                levelIn.updateNeighborsAt(pos.relative(direction), this);
            }
            for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(levelIn, pos.relative(direction1));
            }
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.relative(direction2);
                if (levelIn.getBlockState(blockpos).isCollisionShapeFullBlock(levelIn, blockpos)) {
                    this.notifyWireNeighborsOfStateChange(levelIn, blockpos.above());
                } else {
                    this.notifyWireNeighborsOfStateChange(levelIn, blockpos.below());
                }
            }

        }
    }

    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onRemove(state, levelIn, pos, newState, isMoving);
            if (!levelIn.isClientSide) {
                for (Direction direction : Direction.values()) {
                    levelIn.updateNeighborsAt(pos.relative(direction), this);
                }
                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.notifyWireNeighborsOfStateChange(levelIn, pos.relative(direction1));
                }
                for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                    BlockPos blockpos = pos.relative(direction2);
                    if (levelIn.getBlockState(blockpos).isCollisionShapeFullBlock(levelIn, blockpos)) {
                        this.notifyWireNeighborsOfStateChange(levelIn, blockpos.above());
                    } else {
                        this.notifyWireNeighborsOfStateChange(levelIn, blockpos.below());
                    }
                }

            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!levelIn.isClientSide) {
            if (!state.canSurvive(levelIn, pos)) {
                dropResources(state, levelIn, pos);
                levelIn.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor levelIn, BlockPos pos, int flags, int recursionLeft) {
        BlockPos.MutableBlockPos pool = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.getValue(RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(direction));
            if (redstoneside != RedstoneSide.NONE && levelIn.getBlockState(pool.set(pos).move(direction)).getBlock() != this) {
                pool.move(Direction.DOWN);
                BlockState blockstate = levelIn.getBlockState(pool);
                if (blockstate.getBlock() != Blocks.OBSERVER) {
                    BlockPos blockpos = pool.relative(direction.getOpposite());
                    BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), levelIn.getBlockState(blockpos), levelIn, pool, blockpos);
                    updateOrDestroy(blockstate, blockstate1, levelIn, pool, flags);
                }

                pool.set(pos).move(direction).move(Direction.UP);
                BlockState blockstate3 = levelIn.getBlockState(pool);
                if (blockstate3.getBlock() != Blocks.OBSERVER) {
                    BlockPos blockpos1 = pool.relative(direction.getOpposite());
                    BlockState blockstate2 = blockstate3.updateShape(direction.getOpposite(), levelIn.getBlockState(blockpos1), levelIn, pool, blockpos1);
                    updateOrDestroy(blockstate3, blockstate2, levelIn, pool, flags);
                }
            }
        }

    }

    private void notifyWireNeighborsOfStateChange(Level levelIn, BlockPos pos) {
        if (levelIn.getBlockState(pos).getBlock() == this) {
            levelIn.updateNeighborsAt(pos, this);

            for (Direction direction : Direction.values()) {
                levelIn.updateNeighborsAt(pos.relative(direction), this);
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
        return RenderType::cutoutMipped;
    }
}
