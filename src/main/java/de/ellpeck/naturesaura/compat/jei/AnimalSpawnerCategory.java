package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerRecipe> {

    private final IDrawable background;

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    @Override
    public ResourceLocation getUid() {
        return JEINaturesAuraPlugin.SPAWNER;
    }

    @Override
    public Class<? extends AnimalSpawnerRecipe> getRecipeClass() {
        return AnimalSpawnerRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.SPAWNER + ".name");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(AnimalSpawnerRecipe animalSpawnerRecipe, IIngredients iIngredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, AnimalSpawnerRecipe recipe, IIngredients iIngredients) {
        IGuiItemStackGroup group = iRecipeLayout.getItemStacks();
        for (int i = 0; i < recipe.ingredients.length; i++) {
            group.init(i, true, i * 18, 68);
            group.set(i, Arrays.asList(recipe.ingredients[i].getMatchingStacks()));
        }
    }
}
