package de.ellpeck.naturesaura.entities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class RenderMoverMinecart extends MinecartRenderer<EntityMoverMinecart> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/mover_cart.png");

    public RenderMoverMinecart(EntityRendererProvider.Context p_174300_) {
        super(p_174300_, ModelLayers.MINECART);
    }
    //private final ModelMoverMinecart model = new ModelMoverMinecart();

    @Override
    protected void renderMinecartContents(EntityMoverMinecart entityIn, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 22 / 16F, 0);
        matrixStackIn.translate(0, 0, 1);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        //this.model.render(matrixStackIn, bufferIn.getBuffer(this.model.getRenderType(RES)), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.popPose();
    }

    // TODO model rendering
/*    private static class ModelMoverMinecart extends Model {

        private final ModelRenderer box;

        public ModelMoverMinecart() {
            super(RenderType::getEntityCutout);
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 24, 16);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }*/
}
