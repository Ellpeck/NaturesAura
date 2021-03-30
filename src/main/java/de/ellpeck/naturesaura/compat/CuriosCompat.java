package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CuriosCompat implements ICompat {

    private static final Map<Item, String> TYPES = ImmutableMap.<Item, String>builder()
            .put(ModItems.EYE, "charm")
            .put(ModItems.EYE_IMPROVED, "charm")
            .put(ModItems.AURA_CACHE, "belt")
            .put(ModItems.AURA_TROVE, "belt")
            .put(ModItems.SHOCKWAVE_CREATOR, "necklace")
            .put(ModItems.DEATH_RING, "ring")
            .build();

    @Override
    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onCapabilitiesAttach);
    }

    @Override
    public void setupClient() {

    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(t).build()));
    }

    private void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TYPES.containsKey(stack.getItem())) {
            event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "curios"), new ICapabilityProvider() {
                private final LazyOptional<ICurio> curio = LazyOptional.of(() -> new ICurio() {
                    @Override
                    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                        stack.getItem().inventoryTick(stack, livingEntity.world, livingEntity, -1, false);

                    }

                    @Override
                    public boolean canEquipFromUse(SlotContext slotContext) {
                        return true;
                    }

                    @Override
                    public boolean canSync(String identifier, int index, LivingEntity livingEntity) {
                        return true;
                    }
                });

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap != CuriosCapability.ITEM)
                        return LazyOptional.empty();
                    return this.curio.cast();
                }
            });
        }
    }

    @Override
    public void addItemTags(ItemTagProvider provider) {
        for (Map.Entry<Item, String> entry : TYPES.entrySet()) {
            ITag.INamedTag<Item> tag = ItemTags.createOptional(new ResourceLocation("curios", entry.getValue()));
            provider.getOrCreateBuilder(tag).add(entry.getKey());
        }
    }
}
