package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGeneratorLimitRemover extends TileEntityRenderer<TileEntityGeneratorLimitRemover> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    @Override
    public void render(TileEntityGeneratorLimitRemover te, double x, double y, double z, float partialTicks, int destroyStage) {
        TileEntity above = te.getWorld().getTileEntity(te.getPos().up());
        if (above instanceof TileEntityImpl && ((TileEntityImpl) above).wantsLimitRemover()) {
            this.renderGlint(x, y + 1, z);
            this.renderGlint(x, y, z);
        }
    }

    private void renderGlint(double x, double y, double z) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.depthMask(false);
        int brightness = 15 << 20 | 15 << 4;
        int j = brightness % 65536;
        int k = brightness / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
        float alpha = ((float) Math.sin(System.currentTimeMillis() / 800D) + 1F) / 2F;
        GlStateManager.color4f(alpha, alpha, alpha, alpha);
        GlStateManager.translated(x - 0.001F, y + 1 + 0.001F, z + 1 + 0.001F);
        GlStateManager.rotatef(180F, 1, 0, 0);
        GlStateManager.scalef(1.002F, 1.002F, 1.002F);
        this.bindTexture(RES);
        this.model.render();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static class ModelLimitRemoverGlint extends Model {

        private final RendererModel box;

        public ModelLimitRemoverGlint() {
            this.box = new RendererModel(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        public void render() {
            this.box.render(1 / 16F);
        }
    }
}
