package de.ellpeck.naturesaura.compat.jei.treeritual;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class TreeRitualWrapper implements IRecipeWrapper {

    public final TreeRitualRecipe recipe;

    public TreeRitualWrapper(TreeRitualRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Ingredient ing : this.recipe.ingredients)
            builder.add(ing.getMatchingStacks());
        builder.add(this.recipe.saplingType.getMatchingStacks());
        ingredients.setInputs(VanillaTypes.ITEM, builder.build());
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.result);
    }
}
