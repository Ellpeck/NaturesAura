package de.ellpeck.naturesaura.jei.altar;

import de.ellpeck.naturesaura.recipes.AltarRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class AltarWrapper implements IRecipeWrapper {

    public final AltarRecipe recipe;

    public AltarWrapper(AltarRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, this.recipe.input);
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
    }
}
