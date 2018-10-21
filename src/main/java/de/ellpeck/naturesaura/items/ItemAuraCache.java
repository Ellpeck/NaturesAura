package de.ellpeck.naturesaura.items;

import baubles.api.render.IRenderBauble;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.aura.container.ItemAuraContainer;
import de.ellpeck.naturesaura.renderers.ITrinketItem;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets.RenderType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemAuraCache extends ItemImpl implements ITrinketItem {

    public ItemAuraCache() {
        super("aura_cache");
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stackIn, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.isSneaking()) {
                ItemStack stack = player.getHeldItemMainhand();
                if (stack.hasCapability(Capabilities.auraRecharge, null)) {
                    IAuraContainer container = stackIn.getCapability(Capabilities.auraContainer, null);
                    if (container.getStoredAura() >= 3 && stack.getCapability(Capabilities.auraRecharge, null).recharge()) {
                        container.drainAura(4, false);
                    }
                }
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            ItemStack stack = new ItemStack(this);
            IAuraContainer container = stack.getCapability(Capabilities.auraContainer, null);
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
        if (stack.hasCapability(Capabilities.auraContainer, null)) {
            IAuraContainer container = stack.getCapability(Capabilities.auraContainer, null);
            return 1 - container.getStoredAura() / (double) container.getMaxAura();
        } else {
            return 0;
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ICapabilityProvider() {
            private final ItemAuraContainer container = new ItemAuraContainer(stack, 4000, true);

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == Capabilities.auraContainer;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == Capabilities.auraContainer) {
                    return (T) this.container;
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(ItemStack stack, EntityPlayer player, RenderType type) {
        if (type == RenderType.BODY) {
            boolean armor = !player.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex()).isEmpty();
            IRenderBauble.Helper.rotateIfSneaking(player);
            GlStateManager.translate(-0.15F, 0.65F, armor ? -0.195F : -0.13F);
            GlStateManager.scale(0.25F, 0.25F, 0.25F);
            GlStateManager.rotate(180F, 1F, 0F, 0F);
            Helper.renderItemInWorld(stack);
        }
    }
}
