package de.ellpeck.naturesaura.entities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class RenderMoverMinecart extends MinecartRenderer<EntityMoverMinecart> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/mover_cart.png");

    public RenderMoverMinecart(EntityRendererProvider.Context p_174300_) {
        super(p_174300_, ModelLayers.MINECART);
    }

    private final ModelMoverMinecart model = new ModelMoverMinecart();

    @Override
    protected void renderMinecartContents(EntityMoverMinecart entityIn, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 22 / 16F, 0);
        matrixStackIn.translate(0, 0, 1);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        this.model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(this.model.renderType(RenderMoverMinecart.RES)), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.popPose();
    }

    private static class ModelMoverMinecart extends Model {

        private final ModelPart model;

        public ModelMoverMinecart() {
            super(RenderType::entityCutout);
            var mesh = new MeshDefinition();
            var part = mesh.getRoot();
            part.addOrReplaceChild("main", new CubeListBuilder().addBox(0, 0, 0, 16, 24, 16), PartPose.ZERO);
            this.model = LayerDefinition.create(mesh, 64, 64).bakeRoot();
        }

        @Override
        public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
