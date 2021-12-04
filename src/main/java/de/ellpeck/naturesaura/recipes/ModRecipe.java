package de.ellpeck.naturesaura.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class ModRecipe implements IRecipe<RecipeWrapper> {

    public final ResourceLocation name;

    public ModRecipe(ResourceLocation name) {
        this.name = name;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level levelIn) {
        // return true here so that we can easily get all recipes of a type from the recipe manager
        return true;
    }

    @Override
    public ItemStack getCraftingResult(RecipeWrapper inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return this.name;
    }
}
