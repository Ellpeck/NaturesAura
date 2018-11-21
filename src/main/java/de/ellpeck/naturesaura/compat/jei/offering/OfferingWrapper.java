package de.ellpeck.naturesaura.compat.jei.offering;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class OfferingWrapper implements IRecipeWrapper {

    public final OfferingRecipe recipe;

    public OfferingWrapper(OfferingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, ImmutableList.<ItemStack>builder()
                .add(this.recipe.input.getMatchingStacks())
                .add(this.recipe.startItem.getMatchingStacks()).build());
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
    }
}
