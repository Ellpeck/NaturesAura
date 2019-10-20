package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemEye extends ItemImpl implements ITrinketItem {

    public ItemEye(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, PlayerEntity player, RenderType type, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
            GlStateManager.translate(0.1F, 0.225F, armor ? -0.195F : -0.1475F);
            GlStateManager.scale(0.15F, 0.15F, 0.15F);
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            Helper.renderItemInWorld(stack);
        }
    }
}
