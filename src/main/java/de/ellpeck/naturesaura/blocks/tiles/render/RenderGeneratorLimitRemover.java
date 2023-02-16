package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover implements BlockEntityRenderer<BlockEntityGeneratorLimitRemover> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderGeneratorLimitRemover(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityGeneratorLimitRemover te, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        var above = te.getLevel().getBlockEntity(te.getBlockPos().above());
        if (above instanceof BlockEntityImpl && ((BlockEntityImpl) above).wantsLimitRemover()) {
            RenderGeneratorLimitRemover.renderGlint(matrixStack, iRenderTypeBuffer, this.model, 0, 1, 0, combinedOverlayIn, RenderGeneratorLimitRemover.RES);
            RenderGeneratorLimitRemover.renderGlint(matrixStack, iRenderTypeBuffer, this.model, 0, 0, 0, combinedOverlayIn, RenderGeneratorLimitRemover.RES);
        }
    }

    public static void renderGlint(PoseStack stack, MultiBufferSource buffer, ModelLimitRemoverGlint model, int xOff, int yOff, int zOff, int combinedOverlayIn, ResourceLocation texture) {
        stack.pushPose();
        var brightness = 15 << 20 | 15 << 4;
        var alpha = ((float) Math.sin(System.currentTimeMillis() / 800D) + 1F) / 2F;
        stack.translate(-0.001F + xOff, 1 + 0.001F + yOff, 1 + 0.001F + zOff);
        stack.mulPose(Vector3f.XP.rotationDegrees(180F));
        stack.scale(1.002F, 1.002F, 1.002F);
        model.renderToBuffer(stack, buffer.getBuffer(model.renderType(texture)), brightness, combinedOverlayIn, 1, 1, 1, alpha);
        stack.popPose();
    }

    public static class ModelLimitRemoverGlint extends Model {

        private final ModelPart model;

        public ModelLimitRemoverGlint() {
            super(RenderType::entityTranslucent);
            var mesh = new MeshDefinition();
            var part = mesh.getRoot();
            part.addOrReplaceChild("main", new CubeListBuilder().addBox(0, 0, 0, 16, 16, 16), PartPose.ZERO);
            this.model = LayerDefinition.create(mesh, 64, 64).bakeRoot();
        }

        @Override
        public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
