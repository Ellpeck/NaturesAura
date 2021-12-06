package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEnderCrate implements BlockEntityRenderer<BlockEntityEnderCrate> {

    @Override
    public void render(BlockEntityEnderCrate tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        float f1 = RenderEnderCrate.getOffsetUp();
        RenderEnderCrate.renderFace(matrix4f, bufferIn.getBuffer(RenderEnderCrate.renderType()), f1, f1);
    }

    private static void renderFace(Matrix4f p_173696_, VertexConsumer p_173697_, float p_173700_, float p_173701_) {
        p_173697_.vertex(p_173696_, (float) 0.0, p_173700_, (float) 1.0).endVertex();
        p_173697_.vertex(p_173696_, (float) 1.0, p_173700_, (float) 1.0).endVertex();
        p_173697_.vertex(p_173696_, (float) 1.0, p_173701_, (float) 0.0).endVertex();
        p_173697_.vertex(p_173696_, (float) 0.0, p_173701_, (float) 0.0).endVertex();
    }

    private static float getOffsetUp() {
        return 1.001F;
    }

    private static RenderType renderType() {
        return RenderType.endPortal();
    }
}
