package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Compat {

    private static final Map<String, Supplier<ICompat>> MODULE_TYPES = ImmutableMap.<String, Supplier<ICompat>>builder()
            .put("patchouli", PatchouliCompat::new)
            .put("curios", CuriosCompat::new)
            //.put("enchantability", EnchantibilityCompat::new)
            .build();
    private static final Map<String, ICompat> MODULES = new HashMap<>();

    public static void setup(FMLCommonSetupEvent event) {
        Compat.populateModules(ModList.get()::isLoaded);
        Compat.MODULES.values().forEach(c -> c.setup(event));
    }

    public static void setupClient() {
        Compat.MODULES.values().forEach(ICompat::setupClient);
    }

    public static boolean hasCompat(String mod) {
        return Compat.MODULES.containsKey(mod);
    }

    public static void addItemTags(ItemTagProvider provider) {
        // since other mods don't get loaded in runData, just populate all modules
        Compat.populateModules(s -> true);
        Compat.MODULES.values().forEach(m -> m.addItemTags(provider));
    }

    private static void populateModules(Predicate<String> isLoaded) {
        for (var entry : Compat.MODULE_TYPES.entrySet()) {
            var id = entry.getKey();
            if (isLoaded.test(id)) {
                Compat.MODULES.put(id, entry.getValue().get());
                NaturesAura.LOGGER.info("Loading compat module for mod " + id);
            }
        }
    }
}
