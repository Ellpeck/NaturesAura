package de.ellpeck.naturesaura.blocks.tiles.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;

public class RenderWoodStand implements BlockEntityRenderer<BlockEntityWoodStand> {

    public RenderWoodStand(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityWoodStand tileEntityWoodStand, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        var stack = tileEntityWoodStand.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            matrixStack.pushPose();
            var item = stack.getItem();
            if (item instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().isSolid()) {
                matrixStack.translate(0.5F, 0.755F, 0.5F);
                var scale = 0.95F;
                matrixStack.scale(scale, scale, scale);
            } else {
                matrixStack.translate(0.5F, 0.825F, 0.4F);
                var scale = 0.75F;
                matrixStack.scale(scale, scale, scale);
                matrixStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, i, i1, matrixStack, iRenderTypeBuffer, tileEntityWoodStand.getLevel(), 0);
            matrixStack.popPose();
        }
    }
}
