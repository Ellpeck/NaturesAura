package de.ellpeck.naturesaura.compat.jei;

import com.google.common.collect.ImmutableList;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerRecipe> {

    private final IDrawable background;
    private final Map<EntityType, Entity> entityCache = new HashMap<>();

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    @Override
    public ResourceLocation getUid() {
        return JEINaturesAuraPlugin.SPAWNER;
    }

    @Override
    public Class<? extends AnimalSpawnerRecipe> getRecipeClass() {
        return AnimalSpawnerRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("container." + JEINaturesAuraPlugin.SPAWNER + ".name");
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
    public void setIngredients(AnimalSpawnerRecipe animalSpawnerRecipe, IIngredients iIngredients) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Ingredient ing : animalSpawnerRecipe.ingredients)
            builder.add(ing.getMatchingStacks());
        iIngredients.setInputs(VanillaTypes.ITEM, builder.build());
        iIngredients.setOutput(VanillaTypes.ITEM, new ItemStack(SpawnEggItem.getEgg(animalSpawnerRecipe.entity)));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, AnimalSpawnerRecipe recipe, IIngredients iIngredients) {
        IGuiItemStackGroup group = iRecipeLayout.getItemStacks();
        for (int i = 0; i < recipe.ingredients.length; i++) {
            group.init(i, true, i * 18, 68);
            group.set(i, Arrays.asList(recipe.ingredients[i].getMatchingStacks()));
        }
    }

    @Override
    public void draw(AnimalSpawnerRecipe recipe, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = this.entityCache.get(recipe.entity);
        if (entity == null) {
            entity = recipe.makeEntity(minecraft.world, 0, 0, 0);
            this.entityCache.put(recipe.entity, entity);
        }

        float size = Math.max(1F, Math.max(recipe.entity.getWidth(), recipe.entity.getHeight()));
        float rot = (minecraft.world.getGameTime() + minecraft.getRenderPartialTicks()) % 360F;
        renderEntity(entity, 35, 28, rot, 100F / size * 0.4F, size * 0.5F);

        String name = recipe.entity.getName().getFormattedText();
        minecraft.fontRenderer.drawStringWithShadow(name, 36 - minecraft.fontRenderer.getStringWidth(name) / 2F, 55, 0xFFFFFF);
    }

    private static void renderEntity(Entity entity, float x, float y, float rotation, float renderScale, float offset) {
        // TODO Render entity
        /*GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color3f(1F, 1F, 1F);
        GlStateManager.translatef(x, y, 50.0F);
        GlStateManager.scalef(-renderScale, renderScale, renderScale);
        GlStateManager.translatef(0F, offset, 0F);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getInstance().getRenderManager().playerViewY = 180.0F;
        Minecraft.getInstance().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);*/
    }
}
