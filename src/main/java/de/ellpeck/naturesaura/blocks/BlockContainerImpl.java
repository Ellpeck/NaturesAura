package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import de.ellpeck.naturesaura.reg.ModTileType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockContainerImpl extends BaseEntityBlock implements IModItem {

    private final String baseName;
    private final ModTileType<BlockEntity> tileType;

    public BlockContainerImpl(String baseName, BlockEntityType.BlockEntitySupplier<BlockEntity> tileSupplier, Block.Properties properties) {
        super(properties);

        this.baseName = baseName;
        this.tileType = new ModTileType<>(tileSupplier, this);

        ModRegistry.add(this);
        ModRegistry.add(this.tileType);

        if (this.hasWaterlogging())
            this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    protected boolean hasWaterlogging() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (this.hasWaterlogging())
            builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.hasWaterlogging() && state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (this.hasWaterlogging() && stateIn.getValue(BlockStateProperties.WATERLOGGED))
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (this.hasWaterlogging()) {
            FluidState state = context.getLevel().getFluidState(context.getClickedPos());
            return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, state.is(FluidTags.WATER) && state.getAmount() == 8);
        }
        return super.getStateForPlacement(context);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.tileType.type.create(pos, state);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);

        BlockEntity tile = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof BlockEntityImpl) {
            for (ItemStack stack : drops) {
                if (stack.getItem() != this.asItem())
                    continue;
                ((BlockEntityImpl) tile).modifyDrop(stack);
                break;
            }
        }
        return drops;
    }

    @Override
    public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl)
                ((BlockEntityImpl) tile).dropInventory();
        }
        super.onPlace(state, levelIn, pos, newState, isMoving);
    }

    @Override
    public void playerDestroy(Level levelIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(levelIn, player, pos, state, te, stack);
        levelIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityImpl)
            ((BlockEntityImpl) tile).loadDataOnPlace(stack);
    }

    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.updateRedstoneState(levelIn, pos);
    }

    private void updateRedstoneState(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl impl) {
                int newPower = level.getBestNeighborSignal(pos);
                if (impl.redstonePower != newPower)
                    level.scheduleTick(pos, this, 4);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel levelIn, BlockPos pos, Random random) {
        if (!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl impl) {
                int newPower = levelIn.getBestNeighborSignal(pos);
                if (impl.redstonePower != newPower)
                    impl.onRedstonePowerChange(newPower);
            }
        }
    }
}