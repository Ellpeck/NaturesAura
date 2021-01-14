package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEye extends ItemImpl implements ITrinketItem {

    public ItemEye(String name) {
        super(name, new Properties().maxStackSize(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, PlayerEntity player, RenderType type, MatrixStack matrices, IRenderTypeBuffer buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
            matrices.translate(0.1F, 0.225F, armor ? -0.195F : -0.1475F);
            matrices.scale(0.3F, 0.3F, 0.3F);
            matrices.rotate(Vector3f.XP.rotationDegrees(180));
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer);
        }
    }
}
