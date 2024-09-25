package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class TreeRitualCategory implements IRecipeCategory<TreeRitualRecipe> {

    private final IDrawable background;

    public TreeRitualCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "textures/gui/jei/tree_ritual.png"), 0, 0, 146, 86);
    }

    @Override
    public RecipeType<TreeRitualRecipe> getRecipeType() {
        return JEINaturesAuraPlugin.TREE_RITUAL;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container." + JEINaturesAuraPlugin.TREE_RITUAL.getUid() + ".name");
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
    public void setRecipe(IRecipeLayoutBuilder builder, TreeRitualRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 35, 35).addItemStacks(Arrays.asList(recipe.saplingType.getItems()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 125, 35).addItemStack(recipe.output);

        var positions = new int[][]{{35, 1}, {35, 69}, {1, 35}, {69, 35}, {12, 12}, {58, 58}, {58, 12}, {12, 58}};
        for (var i = 0; i < recipe.ingredients.size(); i++)
            builder.addSlot(RecipeIngredientRole.INPUT, positions[i][0], positions[i][1]).addItemStacks(Arrays.asList(recipe.ingredients.get(i).getItems()));
    }

}
