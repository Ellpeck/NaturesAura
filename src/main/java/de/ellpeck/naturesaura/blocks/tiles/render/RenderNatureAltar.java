package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderNatureAltar extends TileEntityRenderer<TileEntityNatureAltar> {
    public RenderNatureAltar(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityNatureAltar tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }

    // TODO TESR
    /*@Override
    public void render(TileEntityNatureAltar tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack stack = tileEntityIn.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            float time = tileEntityIn.bobTimer + partialTicks;
            float bob = (float) Math.sin(time / 10F) * 0.1F;
            GlStateManager.translated(x + 0.5F, y + 1.2F + bob, z + 0.5F);
            GlStateManager.rotatef((time * 3) % 360, 0F, 1F, 0F);
            float scale = stack.getItem() instanceof BlockItem ? 0.75F : 0.5F;
            GlStateManager.scalef(scale, scale, scale);
            Helper.renderItemInWorld(stack);
            GlStateManager.popMatrix();
        }
    }*/
}
