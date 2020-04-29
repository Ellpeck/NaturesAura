package de.ellpeck.naturesaura.compat.jei;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class OfferingCategory implements IRecipeCategory<OfferingRecipe> {

    private final IDrawable background;

    public OfferingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/offering.png"), 0, 0, 87, 36);
    }

    @Override
    public ResourceLocation getUid() {
        return JEINaturesAuraPlugin.OFFERING;
    }

    @Override
    public Class<? extends OfferingRecipe> getRecipeClass() {
        return OfferingRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.OFFERING + ".name");
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
    public void setIngredients(OfferingRecipe offeringRecipe, IIngredients iIngredients) {
        iIngredients.setInputs(VanillaTypes.ITEM, ImmutableList.<ItemStack>builder()
                .add(offeringRecipe.input.getMatchingStacks())
                .add(offeringRecipe.startItem.getMatchingStacks()).build());
        iIngredients.setOutput(VanillaTypes.ITEM, offeringRecipe.output);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, OfferingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        group.init(0, true, 0, 14);
        group.set(0, Arrays.asList(recipe.input.getMatchingStacks()));
        group.init(1, false, 65, 14);
        group.set(1, recipe.output);
        group.init(2, true, 27, 0);
        group.set(2, Arrays.asList(recipe.startItem.getMatchingStacks()));
    }
}
