package de.ellpeck.naturesaura.blocks.tiles.render;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityOfferingTable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class RenderOfferingTable extends TileEntitySpecialRenderer<TileEntityOfferingTable> {

    private final Random rand = new Random();

    @Override
    public void render(TileEntityOfferingTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack stack = tile.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            this.rand.setSeed(Item.getIdFromItem(stack.getItem()) + stack.getMetadata());

            int amount = MathHelper.ceil(stack.getCount() / 8F);
            for (int i = 0; i < amount; i++) {
                GlStateManager.pushMatrix();
                Item item = stack.getItem();

                float scale;
                float yOff;
                if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().getRenderLayer() == BlockRenderLayer.SOLID) {
                    scale = 0.4F;
                    yOff = 0.08F;
                } else {
                    scale = 0.25F;
                    yOff = 0F;
                }

                GlStateManager.translate(
                        x + 0.35F + this.rand.nextFloat() * 0.3F,
                        y + 0.9F + yOff + (i * 0.001F),
                        z + 0.35F + this.rand.nextFloat() * 0.3F);
                GlStateManager.rotate(this.rand.nextFloat() * 360F, 0F, 1F, 0F);
                GlStateManager.rotate(90F, 1F, 0F, 0F);
                GlStateManager.scale(scale, scale, scale);

                Helper.renderItemInWorld(stack);
                GlStateManager.popMatrix();
            }
        }
    }
}
