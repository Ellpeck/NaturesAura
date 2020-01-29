package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover extends TileEntityRenderer<TileEntityGeneratorLimitRemover> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderGeneratorLimitRemover(TileEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(TileEntityGeneratorLimitRemover te, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        TileEntity above = te.getWorld().getTileEntity(te.getPos().up());
        if (above instanceof TileEntityImpl && ((TileEntityImpl) above).wantsLimitRemover()) {
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
            super(RenderType::entityTranslucent);
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
