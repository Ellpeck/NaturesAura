package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import de.ellpeck.naturesaura.data.ItemTagProvider;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Compat {

    @SuppressWarnings("Convert2MethodRef") // bleh classloading compat issues
    private static final Map<String, Supplier<ICompat>> MODULE_TYPES = ImmutableMap.<String, Supplier<ICompat>>builder()
        .put("patchouli", () -> new PatchouliCompat())
        // TODO curios?
        //.put("curios", () -> new CuriosCompat())
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

    public static void gatherData(GatherDataEvent event) {
        // since other mods don't get loaded in runData, just populate all modules
        Compat.populateModules(s -> true);
        Compat.MODULES.values().forEach(m -> m.gatherData(event));
    }

    public static void addItemTags(ItemTagProvider provider) {
        Compat.MODULES.values().forEach(m -> m.addItemTags(provider));
    }

    public static void addCapabilities(RegisterCapabilitiesEvent event) {
        Compat.MODULES.values().forEach(c -> c.addCapabilities(event));
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
