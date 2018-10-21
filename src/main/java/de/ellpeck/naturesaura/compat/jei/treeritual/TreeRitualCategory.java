package de.ellpeck.naturesaura.compat.jei.treeritual;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.jei.JEINaturesAuraPlugin;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class TreeRitualCategory implements IRecipeCategory<TreeRitualWrapper> {

    private final IDrawable background;

    public TreeRitualCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/tree_ritual.png"), 0, 0, 146, 86);
    }

    @Override
    public String getUid() {
        return JEINaturesAuraPlugin.TREE_RITUAL;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.TREE_RITUAL + ".name");
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
    public void setRecipe(IRecipeLayout recipeLayout, TreeRitualWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        TreeRitualRecipe recipe = recipeWrapper.recipe;

        group.init(0, true, 34, 34);
        group.set(0, recipe.saplingType);

        group.init(1, true, 124, 34);
        group.set(1, recipe.result);

        int[][] positions = new int[][]{{35, 1}, {35, 69}, {1, 35}, {69, 35}, {12, 12}, {58, 58}, {58, 12}, {12, 58}};
        for (int i = 0; i < recipe.items.length; i++) {
            group.init(i + 2, true, positions[i][0] - 1, positions[i][1] - 1);
            group.set(i + 2, recipe.items[i]);
        }
    }
}
