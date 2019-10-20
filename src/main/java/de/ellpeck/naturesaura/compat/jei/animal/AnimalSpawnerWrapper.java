package de.ellpeck.naturesaura.compat.jei.animal;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class AnimalSpawnerWrapper implements IRecipeWrapper {

    public final AnimalSpawnerRecipe recipe;
    private Entity entity;

    public AnimalSpawnerWrapper(AnimalSpawnerRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Ingredient ing : this.recipe.ingredients)
            builder.add(ing.getMatchingStacks());
        ingredients.setInputs(VanillaTypes.ITEM, builder.build());

        ItemStack egg = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(egg, this.recipe.entity);
        ingredients.setOutput(VanillaTypes.ITEM, egg);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (this.entity == null)
            this.entity = this.recipe.makeEntity(minecraft.world, 0, 0, 0);

        float size = Math.max(1F, Math.max(this.entity.width, this.entity.height));
        float rot = (minecraft.world.getTotalWorldTime() + minecraft.getRenderPartialTicks()) % 360F;
        renderEntity(this.entity, 35, 28, rot, 100F / size * 0.4F, size * 0.5F);

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
