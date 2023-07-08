package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEye extends ItemImpl implements ITrinketItem {

    public ItemEye(String name) {
        super(name, new Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, Player player, RenderType type, PoseStack matrices, MultiBufferSource buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            var armor = !player.getInventory().armor.get(EquipmentSlot.CHEST.getIndex()).isEmpty();
            matrices.translate(0.1F, 0.225F, armor ? -0.195F : -0.1475F);
            matrices.scale(0.3F, 0.3F, 0.3F);
            matrices.mulPose(Axis.XP.rotationDegrees(180));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer, player.level(), 0);
        }
    }
}
