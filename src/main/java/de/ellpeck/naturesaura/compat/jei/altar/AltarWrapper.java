package de.ellpeck.naturesaura.compat.jei.altar;

import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class AltarWrapper implements IRecipeWrapper {

    public final AltarRecipe recipe;

    public AltarWrapper(AltarRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(this.recipe.input, new ItemStack(this.recipe.catalyst)));
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
    }
}
