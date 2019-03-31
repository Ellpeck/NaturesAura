package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.compat.crafttweaker.CraftTweakerCompat;
import de.ellpeck.naturesaura.compat.patchouli.PatchouliCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public final class Compat {

    public static final String CRAFT_TWEAKER = "crafttweaker";
    public static boolean baubles;
    public static boolean craftTweaker;
    public static boolean mtLib;

    public static void preInit() {
        baubles = Loader.isModLoaded("baubles");
        craftTweaker = Loader.isModLoaded(CRAFT_TWEAKER);
        mtLib = Loader.isModLoaded("mtlib");

        if (baubles)
            MinecraftForge.EVENT_BUS.register(new BaublesCompat());

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
