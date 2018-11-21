package de.ellpeck.naturesaura.compat.jei.offering;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
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

public class OfferingCategory implements IRecipeCategory<OfferingWrapper> {

    private final IDrawable background;

    public OfferingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/offering.png"), 0, 0, 87, 36);
    }

    @Override
    public String getUid() {
        return JEINaturesAuraPlugin.OFFERING;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.OFFERING + ".name");
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
    public void setRecipe(IRecipeLayout recipeLayout, OfferingWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        OfferingRecipe recipe = recipeWrapper.recipe;
        group.init(0, true, 0, 14);
        group.set(0, Arrays.asList(recipe.input.getMatchingStacks()));
        group.init(1, false, 65, 14);
        group.set(1, recipe.output);
        group.init(2, true, 27, 0);
        group.set(2, Arrays.asList(recipe.startItem.getMatchingStacks()));
    }
}
