package de.ellpeck.naturesaura.compat.jei.animal;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.compat.jei.JEINaturesAuraPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerWrapper> {

    private final IDrawable background;
    private AnimalSpawnerRecipe recipe;
    private Entity entity;

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    @Override
    public String getUid() {
        return JEINaturesAuraPlugin.SPAWNER;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.SPAWNER + ".name");
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
    public void setRecipe(IRecipeLayout recipeLayout, AnimalSpawnerWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        this.recipe = recipeWrapper.recipe;
        this.entity = null;
        for (int i = 0; i < this.recipe.ingredients.length; i++) {
            group.init(i, true, i * 18, 68);
            group.set(i, Arrays.asList(this.recipe.ingredients[i].getMatchingStacks()));
        }
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if (this.entity == null)
            this.entity = this.recipe.makeEntity(minecraft.world, 0, 0, 0);

        float size = Math.max(1F, Math.max(this.entity.width, this.entity.height));
        renderEntity(this.entity, 35, 28, 35F, 100F / size * 0.4F, size * 0.5F);

        String name = this.entity.getDisplayName().getFormattedText();
        minecraft.fontRenderer.drawString(name, 36 - minecraft.fontRenderer.getStringWidth(name) / 2F, 55, 0xFFFFFF, true);
    }

    private static void renderEntity(Entity entity, float x, float y, float rotation, float renderScale, float offset) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F);
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-renderScale, renderScale, renderScale);
        GlStateManager.translate(0F, offset, 0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
