package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityGeneratorLimitRemover;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGeneratorLimitRemover extends TileEntitySpecialRenderer<TileEntityGeneratorLimitRemover> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/generator_limit_remover_glint.png");
    private final ModelLimitRemoverGlint model = new ModelLimitRemoverGlint();

    @Override
    public void render(TileEntityGeneratorLimitRemover te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TileEntity above = te.getWorld().getTileEntity(te.getPos().up());
        if (above instanceof TileEntityImpl && ((TileEntityImpl) above).wantsLimitRemover()) {
            this.renderGlint(x, y + 1, z);
            this.renderGlint(x, y, z);
        }
    }

    private void renderGlint(double x, double y, double z) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.depthMask(false);
        int brightness = 15 << 20 | 15 << 4;
        int j = brightness % 65536;
        int k = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        float alpha = ((float) Math.sin(Minecraft.getSystemTime() / 800D) + 1F) / 2F;
        GlStateManager.color(alpha, alpha, alpha, alpha);
        GlStateManager.translate(x - 0.001F, y + 1 + 0.001F, z + 1 + 0.001F);
        GlStateManager.rotate(180F, 1, 0, 0);
        GlStateManager.scale(1.002F, 1.002F, 1.002F);
        this.bindTexture(RES);
        this.model.render();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static class ModelLimitRemoverGlint extends ModelBase {

        private final ModelRenderer box;

        public ModelLimitRemoverGlint() {
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        public void render() {
            this.box.render(1 / 16F);
        }
    }
}
