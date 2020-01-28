package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover extends TileEntityRenderer<TileEntityGeneratorLimitRemover> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    //private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    public RenderGeneratorLimitRemover(TileEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(TileEntityGeneratorLimitRemover tileEntityGeneratorLimitRemover, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }

    // TODO TESR
    /*
    @Override
    public void render(TileEntityGeneratorLimitRemover te, double x, double y, double z, float partialTicks, int destroyStage) {
        TileEntity above = te.getWorld().getTileEntity(te.getPos().up());
        if (above instanceof TileEntityImpl && ((TileEntityImpl) above).wantsLimitRemover()) {
            this.renderGlint(x, y + 1, z);
            this.renderGlint(x, y, z);
        }
    }

    private void renderGlint(double x, double y, double z) {
        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableAlphaTest();
        GlStateManager.enableBlend();
        RenderSystem.alphaFunc(516, 0.003921569F);
        GlStateManager.depthMask(false);
        int brightness = 15 << 20 | 15 << 4;
        int j = brightness % 65536;
        int k = brightness / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
        float alpha = ((float) Math.sin(System.currentTimeMillis() / 800D) + 1F) / 2F;
        RenderSystem.color4f(alpha, alpha, alpha, alpha);
        RenderSystem.translated(x - 0.001F, y + 1 + 0.001F, z + 1 + 0.001F);
        RenderSystem.rotatef(180F, 1, 0, 0);
        RenderSystem.scalef(1.002F, 1.002F, 1.002F);
        this.bindTexture(RES);
        this.model.render();
        GlStateManager.depthMask(true);
        RenderSystem.alphaFunc(516, 0.1F);
        RenderSystem.disableAlphaTest();
        GlStateManager.disableBlend();
        RenderSystem.popMatrix();
    }

    private static class ModelLimitRemoverGlint extends Model {

        private final ModelRenderer box;

        public ModelLimitRemoverGlint() {
            super();
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        @Override
        public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3) {
            this.box.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
        }
    }*/
}
