package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityProjectileGenerator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileGenerator implements BlockEntityRenderer<BlockEntityProjectileGenerator> {

    private static final ResourceLocation RES = ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    private final ModelOverlay model = new ModelOverlay();

    public RenderProjectileGenerator(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityProjectileGenerator te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        stack.pushPose();
        if (te.nextSide == Direction.NORTH) {
            stack.mulPose(Axis.YP.rotationDegrees(270));
            stack.translate(-0.002F, 0, -1);
        } else if (te.nextSide == Direction.EAST) {
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.translate(-1.002F, 0, -1);
        } else if (te.nextSide == Direction.SOUTH) {
            stack.mulPose(Axis.YP.rotationDegrees(90));
            stack.translate(-1.002F, 0, 0);
        } else {
            stack.translate(-0.002F, 0, 0);
        }
        var brightness = 15 << 20 | 15 << 4;
        this.model.renderToBuffer(stack, buffer.getBuffer(this.model.renderType(RenderProjectileGenerator.RES)), brightness, combinedOverlayIn, FastColor.ARGB32.colorFromFloat(1, 1, 1, 1));
        stack.popPose();
    }

    private static class ModelOverlay extends Model {

        private final ModelPart model;

        public ModelOverlay() {
            super(RenderType::entityTranslucent);
            var mesh = new MeshDefinition();
            var part = mesh.getRoot();
            part.addOrReplaceChild("main", new CubeListBuilder().addBox(0, 0, 0, 16, 16, 16), PartPose.ZERO);
            this.model = LayerDefinition.create(mesh, 64, 64).bakeRoot();
        }

        @Override
        public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
            this.model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
        }

    }

}
