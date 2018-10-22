package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipe;

public class PageAltar extends PageDoubleRecipe<AltarRecipe> {

    private static final ItemStack ALTAR = new ItemStack(ModBlocks.NATURE_ALTAR);

    @Override
    protected void drawRecipe(AltarRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(PatchouliCompat.GUI_ELEMENTS);
        Gui.drawModalRectWithCustomSizedTexture(recipeX + 12, recipeY, 0, 0, 75, 44, 256, 256);

        this.parent.drawCenteredStringNoShadow(this.getTitle(second), GuiBook.PAGE_WIDTH / 2, recipeY - 10, 0x333333);
        this.renderItem(recipeX + 12 + 30, recipeY + 13, mouseX, mouseY, ALTAR);

        this.renderItem(recipeX + 12 + 4, recipeY + 13, mouseX, mouseY, recipe.input);
        this.renderItem(recipeX + 12 + 56, recipeY + 13, mouseX, mouseY, recipe.output);
    }

    @Override
    protected AltarRecipe loadRecipe(BookEntry entry, String loc) {
        if (loc != null) {
            AltarRecipe recipe = AltarRecipe.RECIPES.get(new ResourceLocation(loc));
            entry.addRelevantStack(recipe.output, this.pageNum);
            return recipe;
        }
        return null;
    }

    @Override
    protected ItemStack getRecipeOutput(AltarRecipe recipe) {
        return recipe.output;
    }

    @Override
    protected int getRecipeHeight() {
        return 60;
    }
}
