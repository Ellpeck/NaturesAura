package de.ellpeck.naturesaura.compat.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaublesCompat {

    private final IBauble eye = stack -> BaubleType.CHARM;
    private final IBauble cache = new IBauble() {
        @Override
        public BaubleType getBaubleType(ItemStack itemstack) {
            return BaubleType.BELT;
        }

        @Override
        public void onWornTick(ItemStack stack, EntityLivingBase player) {
            stack.getItem().onUpdate(stack, player.world, player, -1, false);
        }

        @Override
        public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
            return true;
        }
    };

    @SubscribeEvent
    public void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        Item item = event.getObject().getItem();
        if (item == ModItems.EYE) {
            this.addCap(event, this.eye);
        } else if (item == ModItems.AURA_CACHE) {
            this.addCap(event, this.cache);
        }
    }

    private void addCap(AttachCapabilitiesEvent<ItemStack> event, IBauble type) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "bauble"), new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (T) type : null;
            }
        });
    }
}
