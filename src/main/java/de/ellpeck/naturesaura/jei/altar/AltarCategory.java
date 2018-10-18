package de.ellpeck.naturesaura.jei.altar;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.jei.JEINaturesAuraPlugin;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AltarCategory implements IRecipeCategory<AltarWrapper> {

    private final IDrawable background;
    private final ItemStack altar = new ItemStack(ModBlocks.NATURE_ALTAR);

    public AltarCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/altar.png"), 0, 0, 78, 57);
    }

    @Override
    public String getUid() {
        return JEINaturesAuraPlugin.ALTAR;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.ALTAR + ".name");
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
    public void drawExtras(Minecraft minecraft) {
        Helper.renderItemInGui(this.altar, 26, 19, 1F);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AltarWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        AltarRecipe recipe = recipeWrapper.recipe;
        group.init(0, true, 0, 18);
        group.set(0, recipe.input);
        group.init(1, false, 56, 18);
        group.set(1, recipe.output);
    }
}
