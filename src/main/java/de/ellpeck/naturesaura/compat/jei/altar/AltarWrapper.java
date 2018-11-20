package de.ellpeck.naturesaura.compat.jei.altar;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class AltarWrapper implements IRecipeWrapper {

    public final AltarRecipe recipe;

    public AltarWrapper(AltarRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        builder.add(this.recipe.input.getMatchingStacks());
        if (this.recipe.catalyst != Ingredient.EMPTY)
            builder.add(this.recipe.catalyst.getMatchingStacks());
        ingredients.setInputs(VanillaTypes.ITEM, builder.build());
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
    }
}
