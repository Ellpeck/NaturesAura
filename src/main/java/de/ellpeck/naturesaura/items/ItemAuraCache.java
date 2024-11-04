package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.reg.ICustomCreativeTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class ItemAuraCache extends ItemImpl implements ITrinketItem, ICustomCreativeTab {

    public ItemAuraCache(String name) {
        super(name, new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!levelIn.isClientSide && entityIn instanceof Player player && player.isShiftKeyDown()) {
            var container = stackIn.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
            if (container == null || container.getStoredAura() <= 0)
                return;
            for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                var stack = player.getInventory().getItem(i);
                var recharge = stack.getCapability(NaturesAuraAPI.AURA_RECHARGE_CAPABILITY);
                if (recharge != null) {
                    if (recharge.rechargeFromContainer(container, itemSlot, i, player.getInventory().selected == i))
                        break;
                }
                // TODO fix enchantments, https://gist.github.com/ChampionAsh5357/d895a7b1a34341e19c80870720f9880f#the-enchantment-datapack-object
                /*else if (stack.getEnchantmentLevel(ModEnchantments.AURA_MENDING) > 0) {
                    var mainSize = player.getInventory().items.size();
                    var isArmor = i >= mainSize && i < mainSize + player.getInventory().armor.size();
                    if ((isArmor || player.getInventory().selected == i) && Helper.rechargeAuraItem(stack, container, 1000))
                        break;
                }*/
            }
        }

    }

    @Override
    public List<ItemStack> getCreativeTabItems() {
        var ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this));
        var full = new ItemStack(this);
        var container = full.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
        container.storeAura(container.getMaxAura(), false);
        ret.add(full);
        return ret;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var container = stack.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
        return container != null ? Math.round(container.getStoredAura() / (float) container.getMaxAura() * 13) : 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var cap = stack.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
        return cap != null ? cap.getAuraColor() : super.getBarColor(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, Player player, RenderType type, PoseStack matrices, MultiBufferSource buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            var chest = !player.getInventory().armor.get(EquipmentSlot.CHEST.getIndex()).isEmpty();
            var legs = !player.getInventory().armor.get(EquipmentSlot.LEGS.getIndex()).isEmpty();
            matrices.translate(-0.15F, 0.65F, chest ? -0.195F : legs ? -0.165F : -0.1475F);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.mulPose(Axis.XP.rotationDegrees(180F));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer, player.level(), 0);
        }
    }

}
