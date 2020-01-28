package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderWoodStand extends TileEntityRenderer<TileEntityWoodStand> {

    public RenderWoodStand(TileEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(TileEntityWoodStand tileEntityWoodStand, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }

    // TODO TESR
    /*@Override
    public void render(TileEntityWoodStand tile, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack stack = tile.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            Item item = stack.getItem();
            if (item instanceof BlockItem && ((BlockItem) item).getBlock().getRenderLayer() == BlockRenderLayer.SOLID) {
                GlStateManager.translated(x + 0.5F, y + 0.9735F, z + 0.5F);
                float scale = 0.65F;
                GlStateManager.scalef(scale, scale, scale);
            } else {
                GlStateManager.translated(x + 0.5F, y + 0.825F, z + 0.5F);
                float scale = 0.4F;
                GlStateManager.scalef(scale, scale, scale);
                GlStateManager.rotatef(90F, 1F, 0F, 0F);
            }
            Helper.renderItemInWorld(stack);
            GlStateManager.popMatrix();
        }
    }*/
}
