package de.ellpeck.naturesaura.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEnderCrate extends AbstractContainerMenu {

    public ContainerEnderCrate(MenuType<ContainerEnderCrate> type, int id, Player player, IItemHandler handler) {
        super(type, id);
        int i = (3 - 4) * 18;
        for (int j = 0; j < 3; ++j)
            for (int k = 0; k < 9; ++k)
                this.addSlot(new SlotItemHandler(handler, k + j * 9, 8 + k * 18, 18 + j * 18));
        for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlot(new Slot(player.getInventory(), j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlot(new Slot(player.getInventory(), i1, 8 + i1 * 18, 161 + i));
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 3 * 9) {
                if (!this.moveItemStackTo(itemstack1, 3 * 9, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 0, 3 * 9, false))
                return ItemStack.EMPTY;

            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }

        return itemstack;
    }

}
