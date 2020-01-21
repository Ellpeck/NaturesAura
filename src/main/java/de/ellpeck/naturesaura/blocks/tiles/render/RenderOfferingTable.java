package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityOfferingTable;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class RenderOfferingTable extends TileEntityRenderer<TileEntityOfferingTable> {

    private final Random rand = new Random();

    @Override
    public void render(TileEntityOfferingTable tile, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack stack = tile.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            this.rand.setSeed(Item.getIdFromItem(stack.getItem()) + stack.getDamage());

            int amount = MathHelper.ceil(stack.getCount() / 2F);
            for (int i = 0; i < amount; i++) {
                GlStateManager.pushMatrix();
                Item item = stack.getItem();

                float scale;
                float yOff;
                if (item instanceof BlockItem && ((BlockItem) item).getBlock().getRenderLayer() == BlockRenderLayer.SOLID) {
                    scale = 0.4F;
                    yOff = 0.08F;
                } else {
                    scale = 0.25F;
                    yOff = 0F;
                }

                GlStateManager.translated(
                        x + 0.35F + this.rand.nextFloat() * 0.3F,
                        y + 0.9F + yOff + (i * 0.001F),
                        z + 0.35F + this.rand.nextFloat() * 0.3F);
                GlStateManager.rotatef(this.rand.nextFloat() * 360F, 0F, 1F, 0F);
                GlStateManager.rotatef(90F, 1F, 0F, 0F);
                GlStateManager.scalef(scale, scale, scale);

                Helper.renderItemInWorld(stack);
                GlStateManager.popMatrix();
            }
        }
    }
}
