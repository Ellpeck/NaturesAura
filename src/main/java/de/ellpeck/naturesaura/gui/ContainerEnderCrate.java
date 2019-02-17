package de.ellpeck.naturesaura.gui;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityEnderCrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEnderCrate extends Container {
    private final TileEntityEnderCrate crate;

    public ContainerEnderCrate(EntityPlayer player, TileEntityEnderCrate crate) {
        this.crate = crate;
        IItemHandler handler = this.crate.getItemHandler(null);

        int i = (3 - 4) * 18;
        for (int j = 0; j < 3; ++j)
            for (int k = 0; k < 9; ++k)
                this.addSlotToContainer(new SlotItemHandler(handler, k + j * 9, 8 + k * 18, 18 + j * 18));
        for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(player.inventory, i1, 8 + i1 * 18, 161 + i));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !this.crate.isInvalid();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 3 * 9) {
                if (!this.mergeItemStack(itemstack1, 3 * 9, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(itemstack1, 0, 3 * 9, false))
                return ItemStack.EMPTY;

            if (itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }

        return itemstack;
    }
}
