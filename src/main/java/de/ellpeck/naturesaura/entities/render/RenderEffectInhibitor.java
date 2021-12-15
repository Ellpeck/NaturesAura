package de.ellpeck.naturesaura.entities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderEffectInhibitor extends EntityRenderer<EntityEffectInhibitor> {

    private final Map<ResourceLocation, ItemStack> items = new HashMap<>();

    public RenderEffectInhibitor(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityEffectInhibitor entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(EntityEffectInhibitor entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        var time = entity.renderTicks + entity.getId() + partialTicks;
        var bob = (float) Math.sin(time / 10F) * 0.05F;
        matrixStackIn.translate(0, 0.15F + bob, 0);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(time * 3 % 360));
        var effect = entity.getInhibitedEffect();
        var stack = this.items.computeIfAbsent(effect,
                res -> ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER), effect));
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
        matrixStackIn.popPose();
    }
}
