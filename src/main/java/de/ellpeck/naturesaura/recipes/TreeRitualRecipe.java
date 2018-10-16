package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.Helper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreeRitualRecipe {

    public static final List<TreeRitualRecipe> RECIPES = new ArrayList<>();

    public final ItemStack saplingType;
    public final ItemStack[] items;
    public final ItemStack result;
    public final int time;

    public TreeRitualRecipe(ItemStack saplingType, ItemStack result, int time, ItemStack... items) {
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

    public void add() {
        RECIPES.add(this);
    }
}
