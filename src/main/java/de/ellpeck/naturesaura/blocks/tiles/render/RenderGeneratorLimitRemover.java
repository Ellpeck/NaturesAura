package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover extends BlockEntityRenderer<BlockEntityGeneratorLimitRemover> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderGeneratorLimitRemover(BlockEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(BlockEntityGeneratorLimitRemover te, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        BlockEntity above = te.getLevel().getBlockEntity(te.getPos().up());
        if (above instanceof BlockEntityImpl && ((BlockEntityImpl) above).wantsLimitRemover()) {
            this.renderGlint(matrixStack, iRenderTypeBuffer, 1, combinedOverlayIn);
            this.renderGlint(matrixStack, iRenderTypeBuffer, 0, combinedOverlayIn);
        }
    }

    private void renderGlint(MatrixStack stack, IRenderTypeBuffer buffer, double yOff, int combinedOverlayIn) {
        stack.push();
        int brightness = 15 << 20 | 15 << 4;
        float alpha = ((float) Math.sin(System.currentTimeMillis() / 800D) + 1F) / 2F;
        stack.translate(-0.001F, yOff + 1 + 0.001F, 1 + 0.001F);
        stack.rotate(Vector3f.XP.rotationDegrees(180F));
        stack.scale(1.002F, 1.002F, 1.002F);
        this.model.render(stack, buffer.getBuffer(this.model.getRenderType(RES)), brightness, combinedOverlayIn, 1, 1, 1, alpha);
        stack.pop();
    }

    private static class ModelLimitRemoverGlint extends Model {

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
    }
}
