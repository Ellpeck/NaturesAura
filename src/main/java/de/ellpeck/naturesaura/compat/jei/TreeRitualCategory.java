/*
package de.ellpeck.naturesaura.compat.jei;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class TreeRitualCategory implements IRecipeCategory<TreeRitualRecipe> {

    private final IDrawable background;

    public TreeRitualCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/tree_ritual.png"), 0, 0, 146, 86);
    }

    @Override
    public ResourceLocation getUid() {
        return JEINaturesAuraPlugin.TREE_RITUAL;
    }

    @Override
    public Class<? extends TreeRitualRecipe> getRecipeClass() {
        return TreeRitualRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.TREE_RITUAL + ".name");
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
    public void setIngredients(TreeRitualRecipe treeRitualRecipe, IIngredients iIngredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Ingredient ing : treeRitualRecipe.ingredients)
            builder.add(ing.getMatchingStacks());
        builder.add(treeRitualRecipe.saplingType.getMatchingStacks());
        iIngredients.setInputs(VanillaTypes.ITEM, builder.build());
        iIngredients.setOutput(VanillaTypes.ITEM, treeRitualRecipe.result);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, TreeRitualRecipe treeRitualRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup group = iRecipeLayout.getItemStacks();

        group.init(0, true, 34, 34);
        group.set(0, Arrays.asList(treeRitualRecipe.saplingType.getMatchingStacks()));

        group.init(1, true, 124, 34);
        group.set(1, treeRitualRecipe.result);

        int[][] positions = new int[][]{{35, 1}, {35, 69}, {1, 35}, {69, 35}, {12, 12}, {58, 58}, {58, 12}, {12, 58}};
        for (int i = 0; i < treeRitualRecipe.ingredients.length; i++) {
            group.init(i + 2, true, positions[i][0] - 1, positions[i][1] - 1);
            group.set(i + 2, Arrays.asList(treeRitualRecipe.ingredients[i].getMatchingStacks()));
        }
    }
}
*/
