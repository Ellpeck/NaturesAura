package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.ItemAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAuraCache extends ItemImpl implements ITrinketItem {

    private final int capacity;

    public ItemAuraCache(String name, int capacity) {
        super(name, new Properties().stacksTo(1));
        this.capacity = capacity;
    }

    @Override
    public void inventoryTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!levelIn.isClientSide && entityIn instanceof Player player) {
            if (player.isCrouching() && stackIn.getCapability(NaturesAuraAPI.capAuraContainer).isPresent()) {
                var container = stackIn.getCapability(NaturesAuraAPI.capAuraContainer).orElse(null);
                if (container.getStoredAura() <= 0) {
                    return;
                }
                for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                    var stack = player.getInventory().getItem(i);
                    var recharge = stack.getCapability(NaturesAuraAPI.capAuraRecharge).orElse(null);
                    if (recharge != null) {
                        if (recharge.rechargeFromContainer(container, itemSlot, i, player.getInventory().selected == i))
                            break;
                    } else if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.AURA_MENDING, stack) > 0) {
                        var mainSize = player.getInventory().items.size();
                        var isArmor = i >= mainSize && i < mainSize + player.getInventory().armor.size();
                        if ((isArmor || player.getInventory().selected == i) && Helper.rechargeAuraItem(stack, container, 1000))
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            items.add(new ItemStack(this));

            var stack = new ItemStack(this);
            stack.getCapability(NaturesAuraAPI.capAuraContainer).ifPresent(container -> {
                container.storeAura(container.getMaxAura(), false);
                items.add(stack);
            });
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (stack.getCapability(NaturesAuraAPI.capAuraContainer).isPresent()) {
            var container = stack.getCapability(NaturesAuraAPI.capAuraContainer).orElse(null);
            return (int) (1 - container.getStoredAura() / (double) container.getMaxAura());
        }
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<ItemAuraContainer> container = LazyOptional.of(() -> new ItemAuraContainer(stack, null, ItemAuraCache.this.capacity));

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == NaturesAuraAPI.capAuraContainer) {
                    return this.container.cast();
                } else {
                    return LazyOptional.empty();
                }
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, Player player, RenderType type, PoseStack matrices, MultiBufferSource buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            var chest = !player.getInventory().armor.get(EquipmentSlot.CHEST.getIndex()).isEmpty();
            var legs = !player.getInventory().armor.get(EquipmentSlot.LEGS.getIndex()).isEmpty();
            matrices.translate(-0.15F, 0.65F, chest ? -0.195F : legs ? -0.165F : -0.1475F);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.mulPose(Vector3f.XP.rotationDegrees(180F));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer, 0);
        }
    }
}
