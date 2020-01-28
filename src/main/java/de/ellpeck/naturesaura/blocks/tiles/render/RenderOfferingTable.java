package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityOfferingTable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import java.util.Random;

public class RenderOfferingTable extends TileEntityRenderer<TileEntityOfferingTable> {

    private final Random rand = new Random();

    public RenderOfferingTable(TileEntityRendererDispatcher disp) {
        super(disp);
    }

    @Override
    public void render(TileEntityOfferingTable tileEntityOfferingTable, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {

    }

    // TODO TESR
    /*@Override
    public void render(TileEntityOfferingTable tileEntityOfferingTable, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int j, int i1) {
        ItemStack stack = tileEntityOfferingTable.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            this.rand.setSeed(Item.getIdFromItem(stack.getItem()) + stack.getDamage());

            int amount = MathHelper.ceil(stack.getCount() / 2F);
            for (int i = 0; i < amount; i++) {
                GlStateManager.pushMatrix();
                Item item = stack.getItem();

                float scale;
                float yOff;
                if (item instanceof BlockItem) {
                    scale = 0.4F;
                    yOff = 0.08F;
                } else {
                    scale = 0.25F;
                    yOff = 0F;
                }

                matrixStack.translate(
                        0.35F + this.rand.nextFloat() * 0.3F,
                        0.9F + yOff + i * 0.001F,
                        0.35F + this.rand.nextFloat() * 0.3F);
                matrixStack.rotate(this.rand.nextFloat() * 360F, 0F, 1F, 0F);
                matrixStack.rotate(90F, 1F, 0F, 0F);
                matrixStack.scale(scale, scale, scale);

                Helper.renderItemInWorld(stack);
                GlStateManager.popMatrix();
            }
        }
    }*/
}
