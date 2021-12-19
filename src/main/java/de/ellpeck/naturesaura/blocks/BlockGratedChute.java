package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGratedChute;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockGratedChute extends BlockContainerImpl implements ICustomBlockState, ICustomItemModel {

    public static final DirectionProperty FACING = HopperBlock.FACING;
    private static final VoxelShape INPUT_SHAPE = box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape MIDDLE_SHAPE = box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape INPUT_MIDDLE_SHAPE = Shapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
    private static final VoxelShape COMBINED_SHAPE = Shapes.join(INPUT_MIDDLE_SHAPE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape DOWN_SHAPE = Shapes.or(COMBINED_SHAPE, box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
    private static final VoxelShape EAST_SHAPE = Shapes.or(COMBINED_SHAPE, box(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
    private static final VoxelShape NORTH_SHAPE = Shapes.or(COMBINED_SHAPE, box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(COMBINED_SHAPE, box(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
    private static final VoxelShape WEST_SHAPE = Shapes.or(COMBINED_SHAPE, box(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
    private static final VoxelShape DOWN_RAYTRACE_SHAPE = Hopper.INSIDE;
    private static final VoxelShape EAST_RAYTRACE_SHAPE = Shapes.or(Hopper.INSIDE, box(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
    private static final VoxelShape NORTH_RAYTRACE_SHAPE = Shapes.or(Hopper.INSIDE, box(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
    private static final VoxelShape SOUTH_RAYTRACE_SHAPE = Shapes.or(Hopper.INSIDE, box(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
    private static final VoxelShape WEST_RAYTRACE_SHAPE = Shapes.or(Hopper.INSIDE, box(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

    public BlockGratedChute() {
        super("grated_chute", BlockEntityGratedChute.class, Properties.of(Material.METAL).strength(3.0F, 8.0F).sound(SoundType.METAL));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> COMBINED_SHAPE;
        };
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter levelIn, BlockPos pos) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_RAYTRACE_SHAPE;
            case NORTH -> NORTH_RAYTRACE_SHAPE;
            case SOUTH -> SOUTH_RAYTRACE_SHAPE;
            case WEST -> WEST_RAYTRACE_SHAPE;
            case EAST -> EAST_RAYTRACE_SHAPE;
            default -> Hopper.INSIDE;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!player.isCrouching())
            return InteractionResult.FAIL;
        var tile = levelIn.getBlockEntity(pos);
        if (!(tile instanceof BlockEntityGratedChute chute))
            return InteractionResult.FAIL;
        if (!levelIn.isClientSide) {
            chute.isBlacklist = !chute.isBlacklist;
            chute.sendToClients();
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var newFacing = context.getClickedFace().getOpposite();
        if (newFacing == Direction.UP)
            newFacing = Direction.DOWN;
        return super.getStateForPlacement(context).setValue(FACING, newFacing);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_60457_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityGratedChute) {
            IItemHandler handler = ((BlockEntityGratedChute) tile).getItemHandler();
            var stack = handler.getStackInSlot(0);
            if (stack.isEmpty())
                return 0;
            return Mth.ceil(stack.getCount() / (float) stack.getMaxStackSize() * 15);
        } else
            return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        // noop
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), generator.modLoc("block/" + this.getBaseName() + "_down"));
    }
}
