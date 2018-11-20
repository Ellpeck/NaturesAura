package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class TreeRitualRecipe {

    public final ResourceLocation name;
    public final Ingredient saplingType;
    public final Ingredient[] ingredients;
    public final ItemStack result;
    public final int time;

    public TreeRitualRecipe(ResourceLocation name, Ingredient saplingType, ItemStack result, int time, Ingredient... ingredients) {
        this.name = name;
        this.saplingType = saplingType;
        this.ingredients = ingredients;
        this.result = result;
        this.time = time;
    }

    public TreeRitualRecipe register() {
        NaturesAuraAPI.TREE_RITUAL_RECIPES.put(this.name, this);
        return this;
    }
}
