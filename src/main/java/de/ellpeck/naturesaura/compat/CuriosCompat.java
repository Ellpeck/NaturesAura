// TODO curios?
/*
package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    public void addCapabilities(RegisterCapabilitiesEvent event) {
        for (var item : CuriosCompat.TYPES.keySet()) {
            event.registerItem(CuriosCapability.ITEM, (s, c) -> new ICurio() {
                @Override
                public void curioTick(SlotContext slotContext) {
                    s.getItem().inventoryTick(s, slotContext.entity().level(), slotContext.entity(), -1, false);
                }

                @Override
                public ItemStack getStack() {
                    return s;
                }

                @Override
                public boolean canEquipFromUse(SlotContext slotContext) {
                    return true;
                }

                @Override
                public boolean canSync(SlotContext slotContext) {
                    return true;
                }
            }, item);
        }
    }

    @Override
    public void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), new CuriosProvider(event.getGenerator().getPackOutput(), event.getExistingFileHelper(), event.getLookupProvider()));
    }

    @Override
    public void addItemTags(ItemTagProvider provider) {
        for (var entry : CuriosCompat.TYPES.entrySet()) {
            var tag = ItemTags.create(new ResourceLocation("curios", entry.getValue()));
            provider.tag(tag).add(entry.getKey());
        }
    }

    private static class CuriosProvider extends CuriosDataProvider {

        public CuriosProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
            super(NaturesAura.MOD_ID, output, fileHelper, registries);
        }

        @Override
        public void generate(HolderLookup.Provider provider, ExistingFileHelper existingFileHelper) {
            for (var type : CuriosCompat.TYPES.values()) {
                this.createSlot(type);
            }
        }

    }

}
*/
