package de.ellpeck.naturesaura.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaublesCompat {

    @SubscribeEvent
    public void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        Item item = event.getObject().getItem();
        if (item == ModItems.EYE || item == ModItems.EYE_IMPROVED)
            this.addCap(event, stack -> BaubleType.CHARM);
        else if (item == ModItems.AURA_CACHE)
            this.addCap(event, new UpdatingBauble(BaubleType.BELT, true));
        else if (item == ModItems.AURA_TROVE)
            this.addCap(event, new UpdatingBauble(BaubleType.BELT, true));
        else if (item == ModItems.SHOCKWAVE_CREATOR)
            this.addCap(event, new UpdatingBauble(BaubleType.AMULET, false));

    }

    private void addCap(AttachCapabilitiesEvent<ItemStack> event, IBauble type) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "bauble"), new ICapabilityProvider() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (T) type : null;
            }
        });
    }

    private static class UpdatingBauble implements IBauble {

        private final BaubleType type;
        private final boolean sync;

        public UpdatingBauble(BaubleType type, boolean sync) {
            this.type = type;
            this.sync = sync;
        }

        @Override
        public BaubleType getBaubleType(ItemStack itemstack) {
            return this.type;
        }

        @Override
        public boolean willAutoSync(ItemStack itemstack, LivingEntity player) {
            return this.sync;
        }

        @Override
        public void onWornTick(ItemStack stack, LivingEntity player) {
            stack.getItem().onUpdate(stack, player.world, player, -1, false);
        }
    }
}
