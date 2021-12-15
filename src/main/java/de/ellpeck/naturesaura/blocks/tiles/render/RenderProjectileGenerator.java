package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityProjectileGenerator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileGenerator implements BlockEntityRenderer<BlockEntityProjectileGenerator> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    //private final ModelOverlay model = new ModelOverlay();

    @Override
    public void render(BlockEntityProjectileGenerator te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        stack.pushPose();
        if (te.nextSide == Direction.NORTH) {
            stack.mulPose(Vector3f.YP.rotationDegrees(270));
            stack.translate(-0.002F, 0, -1);
        } else if (te.nextSide == Direction.EAST) {
            stack.mulPose(Vector3f.YP.rotationDegrees(180));
            stack.translate(-1.002F, 0, -1);
        } else if (te.nextSide == Direction.SOUTH) {
            stack.mulPose(Vector3f.YP.rotationDegrees(90));
            stack.translate(-1.002F, 0, 0);
        } else {
            stack.translate(-0.002F, 0, 0);
        }
        int brightness = 15 << 20 | 15 << 4;
        //this.model.render(stack, buffer.getBuffer(this.model.getRenderType(RES)), brightness, combinedOverlayIn, 1, 1, 1, 1);
        stack.popPose();
    }

    // TODO model rendering
/*    private static class ModelOverlay extends Model {

        private final ModelPart box;

        public ModelOverlay() {
            super(RenderType::entityTranslucent);
            this.box = new ModelPart(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.(0, 0, 0, 16, 16, 16);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }*/
}
