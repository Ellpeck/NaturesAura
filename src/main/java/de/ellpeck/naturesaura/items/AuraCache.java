package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.ItemAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuraCache extends ItemImpl implements ITrinketItem {

    private final int capacity;

    public AuraCache(String name, int capacity) {
        super(name, new Properties().maxStackSize(1).group(NaturesAura.CREATIVE_TAB));
        this.capacity = capacity;
    }

    @Override
    public void inventoryTick(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityIn;
            if (player.isSneaking() && stackIn.getCapability(NaturesAuraAPI.capAuraContainer).isPresent()) {
                IAuraContainer container = stackIn.getCapability(NaturesAuraAPI.capAuraContainer).orElse(null);
                if (container.getStoredAura() <= 0) {
                    return;
                }
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack.getCapability(NaturesAuraAPI.capAuraRecharge).isPresent()) {
                        IAuraRecharge recharge = stack.getCapability(NaturesAuraAPI.capAuraRecharge).orElse(null);
                        if (recharge.rechargeFromContainer(container, itemSlot, i, player.inventory.currentItem == i)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            stack.getCapability(NaturesAuraAPI.capAuraContainer).ifPresent(container -> {
                container.storeAura(container.getMaxAura(), false);
                items.add(stack);
            });
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.getCapability(NaturesAuraAPI.capAuraContainer).isPresent()) {
            IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer).orElse(null);
            return 1 - container.getStoredAura() / (double) container.getMaxAura();
        }
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            private final ItemAuraContainer container = new ItemAuraContainer(stack, null, AuraCache.this.capacity);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == NaturesAuraAPI.capAuraContainer) {
                    return LazyOptional.of(() -> (T) this.container);
                } else {
                    return LazyOptional.empty();
                }
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, PlayerEntity player, RenderType type, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean chest = !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
            boolean legs = !player.inventory.armorInventory.get(EquipmentSlotType.LEGS.getIndex()).isEmpty();
            GlStateManager.translatef(-0.15F, 0.65F, chest ? -0.195F : (legs ? -0.165F : -0.1475F));
            GlStateManager.scalef(0.25F, 0.25F, 0.25F);
            GlStateManager.rotatef(180F, 1F, 0F, 0F);
            Helper.renderItemInWorld(stack);
        }
    }
}
