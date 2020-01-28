package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileGenerator extends TileEntityRenderer<TileEntityProjectileGenerator> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    //private final ModelOverlay model = new ModelOverlay();

    public RenderProjectileGenerator(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityProjectileGenerator tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }

    // TODO TESR
    /*@Override
    public void render(TileEntityProjectileGenerator te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.depthMask(false);
        GlStateManager.translated(x, y, z);
        if (te.nextSide == Direction.NORTH) {
            GlStateManager.rotatef(270, 0, 1, 0);
            GlStateManager.translatef(-0.001F, 0, -1);
        } else if (te.nextSide == Direction.EAST) {
            GlStateManager.rotatef(180, 0, 1, 0);
            GlStateManager.translatef(-1.001F, 0, -1);
        } else if (te.nextSide == Direction.SOUTH) {
            GlStateManager.rotatef(90, 0, 1, 0);
            GlStateManager.translatef(-1.001F, 0, 0);
        } else {
            GlStateManager.translatef(-0.001F, 0, 0);
        }
        this.bindTexture(RES);
        int brightness = 15 << 20 | 15 << 4;
        int j = brightness % 65536;
        int k = brightness / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
        this.model.render();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static class ModelOverlay extends Model {

        private final RendererModel box;

        public ModelOverlay() {
            this.box = new RendererModel(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        public void render() {
            this.box.render(1 / 16F);
        }
    }*/
}
