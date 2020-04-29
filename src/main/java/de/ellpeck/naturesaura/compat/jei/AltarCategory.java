package de.ellpeck.naturesaura.compat.jei;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
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
import java.util.Collections;

public class AltarCategory implements IRecipeCategory<AltarRecipe> {

    private final IDrawable background;
    private final ItemStack altar = new ItemStack(ModBlocks.NATURE_ALTAR);

    public AltarCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/altar.png"), 0, 0, 103, 57);
    }

    @Override
    public ResourceLocation getUid() {
        return JEINaturesAuraPlugin.ALTAR;
    }

    @Override
    public Class<? extends AltarRecipe> getRecipeClass() {
        return AltarRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.ALTAR + ".name");
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
    public void setIngredients(AltarRecipe altarRecipe, IIngredients iIngredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        builder.add(altarRecipe.input.getMatchingStacks());
        if (altarRecipe.catalyst != Ingredient.EMPTY)
            builder.add(altarRecipe.catalyst.getMatchingStacks());
        if (altarRecipe.requiredType != null)
            builder.add(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), altarRecipe.requiredType));
        iIngredients.setInputs(VanillaTypes.ITEM, builder.build());
        iIngredients.setOutput(VanillaTypes.ITEM, altarRecipe.output);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, AltarRecipe recipe, IIngredients iIngredients) {
        IGuiItemStackGroup group = iRecipeLayout.getItemStacks();
        group.init(0, true, 0, 18);
        group.set(0, Arrays.asList(recipe.input.getMatchingStacks()));
        group.init(1, false, 80, 18);
        group.set(1, recipe.output);
        group.init(2, true, 26, 18);
        group.set(2, recipe.catalyst == Ingredient.EMPTY ?
                Collections.singletonList(this.altar) : Arrays.asList(recipe.catalyst.getMatchingStacks()));
        group.init(3, true, 51, 18);
        if (recipe.requiredType != null)
            group.set(3, ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), recipe.requiredType));
    }
}
