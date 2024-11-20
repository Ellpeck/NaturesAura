package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEntityBlastFurnaceBooster extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityBlastFurnaceBooster(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLAST_FURNACE_BOOSTER, pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;

        var toUse = 6500;
        if (!this.canUseRightNow(toUse))
            return;

        var below = this.level.getBlockEntity(this.worldPosition.below());
        if (!(below instanceof BlastFurnaceBlockEntity tile))
            return;
        var input = new SingleRecipeInput(tile.getItem(0));
        var recipe = this.level.getRecipeManager().getRecipeFor(BlockEntityFurnaceHeater.getRecipeType(tile), input, this.level).orElse(null);
        if (recipe == null)
            return;
        if (!this.isApplicable(recipe.value().getIngredients()))
            return;

        var data = BlockEntityFurnaceHeater.getFurnaceData(tile);
        var doneDiff = data.get(3) - data.get(2);
        if (doneDiff > 1)
            return;

        if (this.level.random.nextFloat() > 0.45F) {
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 0));
            return;
        }

        var output = tile.getItem(2);
        if (output.getCount() >= output.getMaxStackSize())
            return;

        if (output.isEmpty()) {
            var result = recipe.value().getResultItem(this.level.registryAccess());
            tile.setItem(2, result.copy());
        } else {
            output.grow(1);
        }

        var pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 30, this.worldPosition);
        IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, toUse);

        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
            new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 1));
    }

    private boolean isApplicable(List<Ingredient> ingredients) {
        for (var ing : ingredients) {
            for (var stack : ing.getItems()) {
                if (stack.is(Tags.Items.ORES) || stack.is(Tags.Items.RAW_MATERIALS))
                    return true;
            }
        }
        return false;
    }

    public IItemHandlerModifiable getItemHandler() {
        var below = this.level.getBlockEntity(this.worldPosition.below());
        if (!(below instanceof BlastFurnaceBlockEntity))
            return null;
        var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, below.getBlockPos(), below.getBlockState(), below, Direction.UP);
        if (handler == null)
            return null;
        return new IItemHandlerModifiable() {
            @Override
            public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
                if (handler instanceof IItemHandlerModifiable)
                    ((IItemHandlerModifiable) handler).setStackInSlot(0, stack);
            }

            @Override
            public int getSlots() {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return handler.getStackInSlot(0);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return handler.insertItem(0, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return handler.extractItem(0, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return handler.getSlotLimit(0);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return handler.isItemValid(0, stack);
            }
        };
    }

    @Override
    public void dropInventory() {
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }

}
