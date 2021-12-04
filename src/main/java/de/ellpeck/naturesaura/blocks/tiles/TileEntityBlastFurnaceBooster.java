package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.BlastFurnaceBlockEntity;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockEntityBlastFurnaceBooster extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityBlastFurnaceBooster() {
        super(ModTileEntities.BLAST_FURNACE_BOOSTER);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;

        BlockEntity below = this.level.getBlockEntity(this.worldPosition.down());
        if (!(below instanceof BlastFurnaceBlockEntity))
            return;
        BlastFurnaceBlockEntity tile = (BlastFurnaceBlockEntity) below;
        IRecipe<?> recipe = this.level.getRecipeManager().getRecipe(BlockEntityFurnaceHeater.getRecipeType(tile), tile, this.level).orElse(null);
        if (recipe == null)
            return;
        if (!this.isApplicable(recipe.getIngredients()))
            return;

        IIntArray data = BlockEntityFurnaceHeater.getFurnaceData(tile);
        int doneDiff = data.get(3) - data.get(2);
        if (doneDiff > 1)
            return;

        if (this.level.rand.nextFloat() > 0.45F) {
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                    new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 0));
            return;
        }

        ItemStack output = tile.getStackInSlot(2);
        if (output.getCount() >= output.getMaxStackSize())
            return;

        if (output.isEmpty()) {
            ItemStack result = recipe.getRecipeOutput();
            tile.setInventorySlotContents(2, result.copy());
        } else {
            output.grow(1);
        }

        BlockPos pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 30, this.worldPosition);
        IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, 6500);

        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 1));
    }

    private boolean isApplicable(List<Ingredient> ingredients) {
        for (Ingredient ing : ingredients) {
            for (ItemStack stack : ing.getMatchingStacks()) {
                if (stack.getItem().getTags().stream().anyMatch(t -> t.getPath().startsWith("ores/")))
                    return true;
            }
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable getItemHandler() {
        BlockEntity below = this.level.getBlockEntity(this.worldPosition.down());
        if (!(below instanceof BlastFurnaceBlockEntity))
            return null;
        IItemHandler handler = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
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
}
