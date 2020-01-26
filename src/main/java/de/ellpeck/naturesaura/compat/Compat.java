package de.ellpeck.naturesaura.compat;

import com.google.common.collect.ImmutableMap;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.crafttweaker.CraftTweakerCompat;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import de.ellpeck.naturesaura.misc.ItemTagProvider;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Compat {

    private static final Map<String, Supplier<ICompat>> MODULE_TYPES = ImmutableMap.<String, Supplier<ICompat>>builder()
            .put("patchouli", PatchouliCompat::new)
            .put("curios", CuriosCompat::new)
            .put("crafttweaker", CraftTweakerCompat::new)
            .build();
    private static final Map<String, ICompat> MODULES = new HashMap<>();

    public static void preInit() {
        populateModules(ModList.get()::isLoaded);
        MODULES.values().forEach(ICompat::preInit);
    }

    public static void preInitClient() {
        MODULES.values().forEach(ICompat::preInitClient);
    }

    public static void postInit() {
        MODULES.values().forEach(ICompat::postInit);
    }

    public static boolean hasCompat(String mod) {
        return MODULES.containsKey(mod);
    }

    public static void addItemTags(ItemTagProvider provider) {
        // since other mods don't get loaded in runData, just populate all modules
        populateModules(s -> true);
        MODULES.values().forEach(m -> m.addItemTags(provider));
    }

    private static void populateModules(Predicate<String> isLoaded) {
        for (Map.Entry<String, Supplier<ICompat>> entry : MODULE_TYPES.entrySet()) {
            String id = entry.getKey();
            if (isLoaded.test(id)) {
                MODULES.put(id, entry.getValue().get());
                NaturesAura.LOGGER.info("Loading compat module for mod " + id);
            }
        }
    }
}
