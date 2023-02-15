package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.ITickableBlockEntity;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import de.ellpeck.naturesaura.reg.ModTileType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BlockContainerImpl extends BaseEntityBlock implements IModItem {

    private final String baseName;
    private final Class<? extends BlockEntity> tileClass;
    private final ModTileType<? extends BlockEntity> tileType;

    public BlockContainerImpl(String baseName, Class<? extends BlockEntity> tileClass, Block.Properties properties) {
        super(properties);

        this.baseName = baseName;
        this.tileClass = tileClass;
        this.tileType = new ModTileType<>(this::createBlockEntity, this);

        ModRegistry.ALL_ITEMS.add(this);
        ModRegistry.ALL_ITEMS.add(this.tileType);

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
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return this.hasWaterlogging() && state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (this.hasWaterlogging() && stateIn.getValue(BlockStateProperties.WATERLOGGED))
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (this.hasWaterlogging()) {
            var state = context.getLevel().getFluidState(context.getClickedPos());
            return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, state.is(FluidTags.WATER) && state.getAmount() == 8);
        }
        return super.getStateForPlacement(context);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.createBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (ITickableBlockEntity.class.isAssignableFrom(this.tileClass))
            return ITickableBlockEntity.createTickerHelper(type, this.tileType.type);
        return null;
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
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        var drops = super.getDrops(state, builder);

        var tile = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof BlockEntityImpl) {
            for (var stack : drops) {
                if (stack.getItem() != this.asItem())
                    continue;
                ((BlockEntityImpl) tile).modifyDrop(stack);
                break;
            }
        }
        return drops;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityImpl impl)
            impl.dropInventory();
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityImpl)
            ((BlockEntityImpl) tile).loadDataOnPlace(stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.updateRedstoneState(levelIn, pos);
    }

    private void updateRedstoneState(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            var tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl impl) {
                var newPower = level.getBestNeighborSignal(pos);
                if (impl.redstonePower != newPower)
                    level.scheduleTick(pos, this, 4);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel levelIn, BlockPos pos, RandomSource random) {
        if (!levelIn.isClientSide) {
            var tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl impl) {
                var newPower = levelIn.getBestNeighborSignal(pos);
                if (impl.redstonePower != newPower)
                    impl.onRedstonePowerChange(newPower);
            }
        }
    }

    private BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        try {
            return this.tileClass.getConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot construct block entity from class " + this.tileClass, e);
        }
    }
}