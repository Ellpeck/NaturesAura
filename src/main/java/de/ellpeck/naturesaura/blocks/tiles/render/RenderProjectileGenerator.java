package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityProjectileGenerator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderProjectileGenerator extends TileEntityRenderer<TileEntityProjectileGenerator> {
    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/projectile_generator_overlay.png");
    private final ModelOverlay model = new ModelOverlay();

    @Override
    public void render(TileEntityProjectileGenerator te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
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
        this.model.render();
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
    }
}
