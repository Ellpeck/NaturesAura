package de.ellpeck.naturesaura.entities.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderEffectInhibitor extends EntityRenderer<EntityEffectInhibitor> {

    private final Map<ResourceLocation, ItemStack> items = new HashMap<>();

    public RenderEffectInhibitor(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityEffectInhibitor entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    @Override
    public void render(EntityEffectInhibitor entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        float time = entity.renderTicks + entity.getEntityId() + partialTicks;
        float bob = (float) Math.sin(time / 10F) * 0.05F;
        matrixStackIn.translate(0, 0.15F + bob, 0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(time * 3 % 360));
        ResourceLocation effect = entity.getInhibitedEffect();
        ItemStack stack = this.items.computeIfAbsent(effect,
                res -> ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER), effect));
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.DEFAULT_LIGHT, matrixStackIn, bufferIn);
        matrixStackIn.pop();
    }
}
