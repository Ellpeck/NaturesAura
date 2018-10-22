package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;

public class PageTreeRitual extends PageDoubleRecipe<TreeRitualRecipe> {

    @Override
    protected void drawRecipe(TreeRitualRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(PatchouliCompat.GUI_ELEMENTS);
        Gui.drawModalRectWithCustomSizedTexture(recipeX - 13, recipeY, 0, 44, 122, 88, 256, 256);

        this.parent.drawCenteredStringNoShadow(this.getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, 0x333333);

        int[][] positions = new int[][]{{38, 4}, {38, 68}, {6, 36}, {70, 36}, {13, 11}, {63, 61}, {63, 11}, {13, 61}};
        for (int i = 0; i < recipe.items.length; i++) {
            ItemStack input = recipe.items[i];
            this.renderItem(recipeX - 13 + positions[i][0], recipeY + positions[i][1], mouseX, mouseY, input);
        }

        this.renderItem(recipeX - 13 + 38, recipeY + 36, mouseX, mouseY, recipe.saplingType);
        this.renderItem(recipeX - 13 + 102, recipeY + 36, mouseX, mouseY, recipe.result);
    }

    @Override
    protected TreeRitualRecipe loadRecipe(BookEntry entry, String loc) {
        if (loc != null) {
            TreeRitualRecipe recipe = TreeRitualRecipe.RECIPES.get(new ResourceLocation(loc));
            entry.addRelevantStack(recipe.result, this.pageNum);
            return recipe;
        }
        return null;
    }

    @Override
    protected ItemStack getRecipeOutput(TreeRitualRecipe recipe) {
        return recipe.result;
    }

    @Override
    protected int getRecipeHeight() {
        return 105;
    }
}
