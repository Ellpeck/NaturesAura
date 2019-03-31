package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileGenerator extends TileEntitySpecialRenderer<TileEntityProjectileGenerator> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    private final ModelOverlay model = new ModelOverlay();

    @Override
    public void render(TileEntityProjectileGenerator te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        if (te.nextSide == EnumFacing.NORTH) {
            GlStateManager.rotate(270, 0, 1, 0);
            GlStateManager.translate(-0.001F, 0, -1);
        } else if (te.nextSide == EnumFacing.EAST) {
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.translate(-1.001F, 0, -1);
        } else if (te.nextSide == EnumFacing.SOUTH) {
            GlStateManager.rotate(90, 0, 1, 0);
            GlStateManager.translate(-1.001F, 0, 0);
        } else {
            GlStateManager.translate(-0.001F, 0, 0);
        }
        this.bindTexture(RES);
        this.model.render();
        GlStateManager.popMatrix();
    }

    private static class ModelOverlay extends ModelBase {

        private final ModelRenderer box;

        public ModelOverlay() {
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 16, 16);
        }

        public void render() {
            this.box.render(1 / 16F);
        }
    }
}
