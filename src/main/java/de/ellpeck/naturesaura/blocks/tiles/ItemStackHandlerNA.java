package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemStackHandlerNA extends ItemStackHandler {

    private final BlockEntityImpl tile;
    private final boolean sendToClients;

    public ItemStackHandlerNA(int size) {
        this(size, null, false);
    }

    public ItemStackHandlerNA(int size, BlockEntityImpl tile, boolean sendToClients) {
        super(size);
        this.tile = tile;
        this.sendToClients = sendToClients;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (this.tile != null) {
            this.tile.markDirty();
            if (this.sendToClients && !this.tile.getLevel().isClientSide)
                this.tile.sendToClients();
        }
    }

    protected boolean canInsert(ItemStack stack, int slot) {
        return true;
    }

    protected boolean canExtract(ItemStack stack, int slot, int amount) {
        return true;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.canInsert(stack, slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.canInsert(stack, slot)) {
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.canExtract(this.getStackInSlot(slot), slot, amount)) {
            return super.extractItem(slot, amount, simulate);
        } else {
            return ItemStack.EMPTY;
        }
    }
}
