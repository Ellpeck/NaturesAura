package de.ellpeck.naturesaura.compat.jei.animal;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class AnimalSpawnerWrapper implements IRecipeWrapper {

    public final AnimalSpawnerRecipe recipe;

    public AnimalSpawnerWrapper(AnimalSpawnerRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Ingredient ing : this.recipe.ingredients)
            builder.add(ing.getMatchingStacks());
        ingredients.setInputs(VanillaTypes.ITEM, builder.build());

        ItemStack egg = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(egg, this.recipe.entity);
        ingredients.setOutput(VanillaTypes.ITEM, egg);
    }
}
