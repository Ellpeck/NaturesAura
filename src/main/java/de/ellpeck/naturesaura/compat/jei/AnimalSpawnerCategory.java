package de.ellpeck.naturesaura.compat.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnimalSpawnerCategory implements IRecipeCategory<AnimalSpawnerRecipe> {

    private final IDrawable background;
    private final Map<EntityType<?>, Entity> entityCache = new HashMap<>();

    public AnimalSpawnerCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation(NaturesAura.MOD_ID, "textures/gui/jei/animal_spawner.png"), 0, 0, 72, 86);
    }

    private static void renderEntity(PoseStack matrixstack, int x, int y, float scale, float yaw, float pitch, LivingEntity entity) {
        var f = (float) Math.atan(yaw / 40.0F);
        var f1 = (float) Math.atan(pitch / 40.0F);
        var posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(x, y, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale(scale, scale, scale);
        var quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        var quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        matrixstack.mulPose(quaternion);
        var f2 = entity.yBodyRot;
        var f3 = entity.getYRot();
        var f4 = entity.getXRot();
        var f5 = entity.yHeadRotO;
        var f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        var entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        var buff = Minecraft.getInstance().renderBuffers().bufferSource();
        entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, buff, 15728880);
        buff.endBatch();
        entityrenderermanager.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public RecipeType<AnimalSpawnerRecipe> getRecipeType() {
        return JEINaturesAuraPlugin.SPAWNER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container." + JEINaturesAuraPlugin.SPAWNER.getUid() + ".name");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AnimalSpawnerRecipe recipe, IFocusGroup focuses) {
        for (var i = 0; i < recipe.ingredients.length; i++)
            builder.addSlot(RecipeIngredientRole.INPUT, i * 18 + 1, 69).addItemStacks(Arrays.asList(recipe.ingredients[i].getItems()));
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(new ItemStack(ForgeSpawnEggItem.fromEntityType(recipe.entity)));
    }

    @Override
    public void draw(AnimalSpawnerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        var minecraft = Minecraft.getInstance();
        var entity = this.entityCache.get(recipe.entity);
        if (entity == null) {
            entity = recipe.makeEntity(minecraft.level, BlockPos.ZERO);
            this.entityCache.put(recipe.entity, entity);
        }

        matrixStack.pushPose();
        var size = Math.max(1F, Math.max(recipe.entity.getWidth(), recipe.entity.getHeight()));
        AnimalSpawnerCategory.renderEntity(matrixStack, 36, 56, 100F / size * 0.4F, 40, size * 0.5F, (LivingEntity) entity);
        matrixStack.popPose();

        var name = recipe.entity.getDescription().getString();
        minecraft.font.drawShadow(matrixStack, name, 36 - minecraft.font.width(name) / 2F, 55, 0xFFFFFF);

    }
}
