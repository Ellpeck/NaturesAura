package de.ellpeck.naturesaura.items;

import net.minecraft.world.item.ItemStack;

public class ItemGlowing extends ItemImpl {

    public ItemGlowing(String baseName) {
        super(baseName);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
