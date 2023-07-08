package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityNatureAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class RenderNatureAltar implements BlockEntityRenderer<BlockEntityNatureAltar> {

    public RenderNatureAltar(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityNatureAltar tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        var stack = tileEntityIn.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            matrixStackIn.pushPose();
            var time = tileEntityIn.bobTimer + partialTicks;
            var bob = (float) Math.sin(time / 10F) * 0.1F;
            matrixStackIn.translate(0.5F, 1.2F + bob, 0.5F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(time * 3 % 360));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, tileEntityIn.getLevel(), 0);
            matrixStackIn.popPose();
        }
    }
}
