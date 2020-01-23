package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import de.ellpeck.naturesaura.reg.ModTileType;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class BlockContainerImpl extends ContainerBlock implements IModItem, IModelProvider {

    private final String baseName;
    private final ModTileType<? extends TileEntity> tileType;

    public BlockContainerImpl(String baseName, Supplier<TileEntity> tileSupplier, Block.Properties properties) {
        super(properties);

        this.baseName = baseName;
        this.tileType = new ModTileType<>(tileSupplier, this);

        ModRegistry.add(this);
        ModRegistry.add(this.tileType);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
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
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);

        TileEntity tile = builder.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof TileEntityImpl) {
            for (ItemStack stack : drops) {
                if (stack.getItem() != this.asItem())
                    continue;
                ((TileEntityImpl) tile).modifyDrop(stack);
                break;
            }
        }
        return drops;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityImpl)
                ((TileEntityImpl) tile).dropInventory();
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityImpl)
            ((TileEntityImpl) tile).loadDataOnPlace(stack);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.updateRedstoneState(worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.updateRedstoneState(worldIn, pos);
    }

    private void updateRedstoneState(World world, BlockPos pos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityImpl) {
                TileEntityImpl impl = (TileEntityImpl) tile;
                int newPower = world.getRedstonePowerFromNeighbors(pos);
                if (impl.redstonePower != newPower)
                    world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
            }
        }
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return 4;
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityImpl) {
                TileEntityImpl impl = (TileEntityImpl) tile;
                int newPower = worldIn.getRedstonePowerFromNeighbors(pos);
                if (impl.redstonePower != newPower)
                    impl.onRedstonePowerChange(newPower);
            }
        }
    }
}