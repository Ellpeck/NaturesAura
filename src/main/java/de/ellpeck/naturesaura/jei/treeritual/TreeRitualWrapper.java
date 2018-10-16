package de.ellpeck.naturesaura.jei.treeritual;

import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeRitualWrapper implements IRecipeWrapper {

    public final TreeRitualRecipe recipe;

    public TreeRitualWrapper(TreeRitualRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputs = new ArrayList<>(Arrays.asList(this.recipe.items));
        inputs.add(this.recipe.saplingType);
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.result);
    }
}
