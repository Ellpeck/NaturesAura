package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOfferingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Random;

public class RenderOfferingTable implements BlockEntityRenderer<BlockEntityOfferingTable> {

    private final Random rand = new Random();

    public RenderOfferingTable(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityOfferingTable tileEntityOfferingTable, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        var stack = tileEntityOfferingTable.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            this.rand.setSeed(Item.getId(stack.getItem()) + stack.getDamageValue());

            var amount = Mth.ceil(stack.getCount() / 2F);
            for (var i = 0; i < amount; i++) {
                matrixStack.pushPose();
                var item = stack.getItem();

                float scale;
                float yOff;
                if (item instanceof BlockItem) {
                    scale = 0.5F;
                    yOff = 0.08F;
                } else {
                    scale = 0.35F;
                    yOff = 0F;
                }

                matrixStack.translate(
                        0.35F + this.rand.nextFloat() * 0.3F,
                        0.9F + yOff + i * 0.001F,
                        0.35F + this.rand.nextFloat() * 0.3F);
                matrixStack.mulPose(Axis.YP.rotationDegrees(this.rand.nextFloat() * 360));
                matrixStack.mulPose(Axis.XP.rotationDegrees(90F));
                matrixStack.scale(scale, scale, scale);

                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, combinedLightIn, combinedOverlayIn, matrixStack, iRenderTypeBuffer, tileEntityOfferingTable.getLevel(), 0);
                matrixStack.popPose();
            }
        }
    }
}
