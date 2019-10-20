package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAuraCache extends ItemImpl implements ITrinketItem {

    private final int capacity;

    public ItemAuraCache(String name, int capacity) {
        super(name);
        this.capacity = capacity;
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityIn;
            if (player.isSneaking()) {
                IAuraContainer container = stackIn.getCapability(NaturesAuraAPI.capAuraContainer, null);
                if (container.getStoredAura() <= 0)
                    return;
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack.hasCapability(NaturesAuraAPI.capAuraRecharge, null)) {
                        IAuraRecharge recharge = stack.getCapability(NaturesAuraAPI.capAuraRecharge, null);
                        if (recharge.rechargeFromContainer(container, itemSlot, i, player.inventory.currentItem == i))
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void getSubItems(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null);
            container.storeAura(container.getMaxAura(), false);
            items.add(stack);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.hasCapability(NaturesAuraAPI.capAuraContainer, null)) {
            IAuraContainer container = stack.getCapability(NaturesAuraAPI.capAuraContainer, null);
            return 1 - container.getStoredAura() / (double) container.getMaxAura();
        } else {
            return 0;
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            private final ItemAuraContainer container = new ItemAuraContainer(stack, null, ItemAuraCache.this.capacity);

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
                return capability == NaturesAuraAPI.capAuraContainer;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == NaturesAuraAPI.capAuraContainer) {
                    return (T) this.container;
                } else {
                    return null;
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
            GlStateManager.translate(-0.15F, 0.65F, chest ? -0.195F : (legs ? -0.165F : -0.1475F));
            GlStateManager.scale(0.25F, 0.25F, 0.25F);
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            Helper.renderItemInWorld(stack);
        }
    }
}
