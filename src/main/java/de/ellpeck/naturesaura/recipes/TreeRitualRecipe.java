package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.Helper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeRitualRecipe {

    public static final Map<ResourceLocation, TreeRitualRecipe> RECIPES = new HashMap<>();

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

    public boolean matchesItems(ItemStack sapling, List<ItemStack> items) {
        if (this.saplingType.isItemEqual(sapling)) {
            for (ItemStack ingredient : this.items) {
                if (Helper.getItemIndex(items, ingredient) < 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public TreeRitualRecipe add() {
        RECIPES.put(this.name, this);
        return this;
    }
}
