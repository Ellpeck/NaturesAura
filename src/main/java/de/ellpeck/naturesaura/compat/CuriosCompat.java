package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.ItemTagProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CurioTags;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CuriosCompat implements ICompat {

    private static final Map<Item, Tag<Item>> TYPES = ImmutableMap.<Item, Tag<Item>>builder()
            .put(ModItems.EYE, CurioTags.CHARM)
            .put(ModItems.EYE_IMPROVED, CurioTags.CHARM)
            .put(ModItems.AURA_CACHE, CurioTags.BELT)
            .put(ModItems.AURA_TROVE, CurioTags.BELT)
            .put(ModItems.SHOCKWAVE_CREATOR, CurioTags.NECKLACE)
            .build();

    @Override
    public void preInit() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this); // inter mod comms
        MinecraftForge.EVENT_BUS.register(this); // capabilities
    }

    @SubscribeEvent
    public void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> {
            String path = t.getId().getPath();
            InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage(path));
        });
    }

    @SubscribeEvent
    public void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TYPES.containsKey(stack.getItem())) {
            event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "curios"), new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap != CuriosCapability.ITEM)
                        return LazyOptional.empty();
                    return LazyOptional.of(() -> (T) new ICurio() {
                        @Override
                        public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {
                            stack.getItem().inventoryTick(stack, livingEntity.world, livingEntity, -1, false);
                        }

                        @Override
                        public boolean canRightClickEquip() {
                            return true;
                        }

                        @Override
                        public boolean shouldSyncToTracking(String identifier, LivingEntity livingEntity) {
                            return true;
                        }
                    });
                }
            });
        }
    }

    @Override
    public void preInitClient() {

    }

    @Override
    public void postInit() {

    }

    @Override
    public void addItemTags(ItemTagProvider provider) {
        for (Map.Entry<Item, Tag<Item>> entry : TYPES.entrySet())
            provider.getBuilder(entry.getValue()).add(entry.getKey());
    }
}
