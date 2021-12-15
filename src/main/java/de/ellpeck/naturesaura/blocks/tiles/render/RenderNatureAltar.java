package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityNatureAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

public class RenderNatureAltar implements BlockEntityRenderer<BlockEntityNatureAltar> {

    @Override
    public void render(BlockEntityNatureAltar tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack stack = tileEntityIn.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            matrixStackIn.pushPose();
            float time = tileEntityIn.bobTimer + partialTicks;
            float bob = (float) Math.sin(time / 10F) * 0.1F;
            matrixStackIn.translate(0.5F, 1.2F + bob, 0.5F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(time * 3 % 360));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
    }
}
