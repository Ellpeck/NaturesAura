package de.ellpeck.naturesaura.compat.jei.animal;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.compat.jei.JEINaturesAuraPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerWrapper> {

    private final IDrawable background;

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    @Override
    public String getUid() {
        return JEINaturesAuraPlugin.SPAWNER;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.SPAWNER + ".name");
    }

    @Override
    public String getModName() {
        return NaturesAura.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AnimalSpawnerWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        AnimalSpawnerRecipe recipe = recipeWrapper.recipe;
        for (int i = 0; i < recipe.ingredients.length; i++) {
            group.init(i, true, i * 18, 68);
            group.set(i, Arrays.asList(recipe.ingredients[i].getMatchingStacks()));
        }
    }
}
