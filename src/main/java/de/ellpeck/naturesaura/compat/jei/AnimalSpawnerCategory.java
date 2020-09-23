package de.ellpeck.naturesaura.compat.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerRecipe> {

    private final IDrawable background;
    private final Map<EntityType, Entity> entityCache = new HashMap<>();

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    private static void renderEntity(MatrixStack matrixstack, int x, int y, float scale, float yaw, float pitch, LivingEntity entity) {
        float f = (float) Math.atan(yaw / 40.0F);
        float f1 = (float) Math.atan(pitch / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        entity.renderYawOffset = 180.0F + f * 20.0F;
        entity.rotationYaw = 180.0F + f * 40.0F;
        entity.rotationPitch = -f1 * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl buff = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, buff, 15728880);
        buff.finish();
        entityrenderermanager.setRenderShadow(true);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        RenderSystem.popMatrix();
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
    public void draw(AnimalSpawnerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = this.entityCache.get(recipe.entity);
        if (entity == null) {
            entity = recipe.makeEntity(minecraft.world, BlockPos.ZERO);
            this.entityCache.put(recipe.entity, entity);
        }

        matrixStack.push();
        float size = Math.max(1F, Math.max(recipe.entity.getWidth(), recipe.entity.getHeight()));
        renderEntity(matrixStack, 35, 55, 100F / size * 0.4F, 40, size * 0.5F, (LivingEntity) entity);
        matrixStack.pop();

        String name = recipe.entity.getName().getString();
        minecraft.fontRenderer.drawStringWithShadow(matrixStack, name, 36 - minecraft.fontRenderer.getStringWidth(name) / 2F, 55, 0xFFFFFF);

    }
}
