package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class RenderNatureAltar extends TileEntityRenderer<TileEntityNatureAltar> {
    @Override
    public void render(TileEntityNatureAltar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = tile.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            float time = tile.bobTimer + partialTicks;
            float bob = (float) Math.sin(time / 10F) * 0.1F;
            GlStateManager.translate(x + 0.5F, y + 1.2F + bob, z + 0.5F);
            GlStateManager.rotate((time * 3) % 360, 0F, 1F, 0F);
            float scale = stack.getItem() instanceof BlockItem ? 0.75F : 0.5F;
            GlStateManager.scale(scale, scale, scale);
            Helper.renderItemInWorld(stack);
            GlStateManager.popMatrix();
        }
    }
}
