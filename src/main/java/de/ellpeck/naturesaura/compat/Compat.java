package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.compat.crafttweaker.CraftTweakerCompat;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import net.minecraftforge.fml.ModList;

public final class Compat {

    public static final String CRAFT_TWEAKER = "crafttweaker";
    public static boolean baubles;
    public static boolean craftTweaker;
    public static boolean mtLib;

    public static void preInit() {
        ModList mods = ModList.get();
        baubles = mods.isLoaded("baubles");
        craftTweaker = mods.isLoaded(CRAFT_TWEAKER);
        mtLib = mods.isLoaded("mtlib");

        /*if (baubles)
            MinecraftForge.EVENT_BUS.register(new BaublesCompat());*/

        PatchouliCompat.preInit();
    }

    public static void preInitClient() {
        PatchouliCompat.preInitClient();
    }

    public static void postInit() {
        if (craftTweaker && mtLib)
            CraftTweakerCompat.postInit();
    }
}
