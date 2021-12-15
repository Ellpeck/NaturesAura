package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover implements BlockEntityRenderer<BlockEntityGeneratorLimitRemover> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    //private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderGeneratorLimitRemover(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityGeneratorLimitRemover te, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        var above = te.getLevel().getBlockEntity(te.getBlockPos().above());
        if (above instanceof BlockEntityImpl && ((BlockEntityImpl) above).wantsLimitRemover()) {
            this.renderGlint(matrixStack, iRenderTypeBuffer, 1, combinedOverlayIn);
            this.renderGlint(matrixStack, iRenderTypeBuffer, 0, combinedOverlayIn);
        }
    }

    private void renderGlint(PoseStack stack, MultiBufferSource buffer, double yOff, int combinedOverlayIn) {
        stack.pushPose();
        var brightness = 15 << 20 | 15 << 4;
        var alpha = ((float) Math.sin(System.currentTimeMillis() / 800D) + 1F) / 2F;
        stack.translate(-0.001F, yOff + 1 + 0.001F, 1 + 0.001F);
        stack.mulPose(Vector3f.XP.rotationDegrees(180F));
        stack.scale(1.002F, 1.002F, 1.002F);
        //this.model.render(stack, buffer.getBuffer(this.model.getRenderType(RES)), brightness, combinedOverlayIn, 1, 1, 1, alpha);
        stack.popPose();
    }

    // TODO model rendering
   /* private static class ModelLimitRemoverGlint extends Model {

        private final ModelRenderer box;

        public ModelLimitRemoverGlint() {
            super(RenderType::getEntityTranslucent);
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        @Override
        public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }*/
}
