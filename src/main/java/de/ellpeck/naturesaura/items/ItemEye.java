package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.renderers.ITrinketItem;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets.RenderType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEye extends ItemImpl implements ITrinketItem {

    public ItemEye() {
        super("eye");
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(ItemStack stack, EntityPlayer player, RenderType type, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex()).isEmpty();
            GlStateManager.translate(0.1F, 0.19F, armor ? -0.195F : -0.13F);
            GlStateManager.scale(0.15F, 0.15F, 0.15F);
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            Helper.renderItemInWorld(stack);
        }
    }
}
