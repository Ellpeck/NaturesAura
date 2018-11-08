package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class RenderNatureAltar extends TileEntitySpecialRenderer<TileEntityNatureAltar> {
    @Override
    public void render(TileEntityNatureAltar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = tile.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            float time = tile.bobTimer + partialTicks;
            float bob = (float) Math.sin(time / 10F) * 0.1F;
            GlStateManager.translate(x + 0.5F, y + 1.2F + bob, z + 0.5F);
            GlStateManager.rotate((time * 3) % 360, 0F, 1F, 0F);
            float scale = stack.getItem() instanceof ItemBlock ? 0.75F : 0.5F;
            GlStateManager.scale(scale, scale, scale);
            Helper.renderItemInWorld(stack);
            GlStateManager.popMatrix();
        }
    }
}
