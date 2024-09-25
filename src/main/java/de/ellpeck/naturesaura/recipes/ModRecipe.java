package de.ellpeck.naturesaura.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public abstract class ModRecipe implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(RecipeWrapper inv, Level levelIn) {
        // return true here so that we can easily get all recipes of a type from the recipe manager
        return true;
    }

    @Override
    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

}
