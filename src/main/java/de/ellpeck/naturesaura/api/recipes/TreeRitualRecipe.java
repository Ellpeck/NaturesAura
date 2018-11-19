package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TreeRitualRecipe {

    public final ResourceLocation name;
    public final ItemStack saplingType;
    public final ItemStack[] items;
    public final ItemStack result;
    public final int time;

    public TreeRitualRecipe(ResourceLocation name, ItemStack saplingType, ItemStack result, int time, ItemStack... items) {
        this.name = name;
        this.saplingType = saplingType;
        this.items = items;
        this.result = result;
        this.time = time;
    }

    public boolean matches(ItemStack expected, ItemStack found) {
        return ItemStack.areItemsEqual(expected, found) && ItemStack.areItemStackShareTagsEqual(expected, found);
    }

    public TreeRitualRecipe register() {
        NaturesAuraAPI.TREE_RITUAL_RECIPES.put(this.name, this);
        return this;
    }
}
