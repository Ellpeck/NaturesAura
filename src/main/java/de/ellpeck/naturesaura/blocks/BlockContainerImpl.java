package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import de.ellpeck.naturesaura.reg.ModTileType;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevel;
import net.minecraft.level.Level;
import net.minecraft.level.server.ServerLevel;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class BlockContainerImpl extends BaseEntityBlock implements IModItem {

    private final String baseName;
    private final ModTileType<? extends BlockEntity> tileType;

    public BlockContainerImpl(String baseName, Supplier<BlockEntity> tileSupplier, Block.Properties properties) {
        super(properties);

        this.baseName = baseName;
        this.tileType = new ModTileType<>(tileSupplier, this);

        ModRegistry.add(this);
        ModRegistry.add(this.tileType);

        if (this.hasWaterlogging())
            this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.WATERLOGGED, false));
    }

    protected boolean hasWaterlogging() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        if (this.hasWaterlogging())
            builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.hasWaterlogging() && state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, ILevel levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (this.hasWaterlogging() && stateIn.get(BlockStateProperties.WATERLOGGED))
            levelIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(levelIn));
        return super.updatePostPlacement(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (this.hasWaterlogging()) {
            FluidState state = context.getLevel().getFluidState(context.getPos());
            return this.getDefaultState().with(BlockStateProperties.WATERLOGGED, state.isTagged(FluidTags.WATER) && state.getLevel() == 8);
        }
        return super.getStateForPlacement(context);
    }

    @Nullable
    @Override
    public BlockEntity createNewBlockEntity(IBlockReader levelIn) {
        return this.tileType.type.create();
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlayerDestroy(ILevel levelIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(levelIn, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);

        BlockEntity tile = builder.get(LootParameters.BLOCK_ENTITY);
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
    public void onReplaced(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl)
                ((BlockEntityImpl) tile).dropInventory();
        }
        super.onReplaced(state, levelIn, pos, newState, isMoving);
    }

    @Override
    public void harvestBlock(Level levelIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.harvestBlock(levelIn, player, pos, state, te, stack);
        levelIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public void onBlockPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityImpl)
            ((BlockEntityImpl) tile).loadDataOnPlace(stack);
    }

    @Override
    public void onBlockAdded(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.updateRedstoneState(levelIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.updateRedstoneState(levelIn, pos);
    }

    private void updateRedstoneState(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl) {
                BlockEntityImpl impl = (BlockEntityImpl) tile;
                int newPower = level.getRedstonePowerFromNeighbors(pos);
                if (impl.redstonePower != newPower)
                    level.getPendingBlockTicks().scheduleTick(pos, this, 4);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel levelIn, BlockPos pos, Random random) {
        if (!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityImpl) {
                BlockEntityImpl impl = (BlockEntityImpl) tile;
                int newPower = levelIn.getRedstonePowerFromNeighbors(pos);
                if (impl.redstonePower != newPower)
                    impl.onRedstonePowerChange(newPower);
            }
        }
    }
}