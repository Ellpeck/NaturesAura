package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class RenderWoodStand extends TileEntitySpecialRenderer<TileEntityWoodStand> {
    @Override
    public void render(TileEntityWoodStand tile, double x, double y, double z, float par5, int par6, float f) {
        ItemStack stack = tile.stack;
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.5F, (float) y + 1.125F, (float) z + 0.5F);

            double boop = Minecraft.getSystemTime() / 800D;
            GlStateManager.translate(0D, Math.sin(boop % (2 * Math.PI)) * 0.04, 0D);
            GlStateManager.rotate((float) (((boop * 40D) % 360)), 0, 1, 0);

            float scale = stack.getItem() instanceof ItemBlock ? 0.45F : 0.35F;
            GlStateManager.scale(scale, scale, scale);
            Helper.renderItemInWorld(stack);

            GlStateManager.popMatrix();
        }
    }
}
