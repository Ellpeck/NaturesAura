package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliCompat {

    public static Class bookGuiClass;

    public static void preInit() {
        PatchouliAPI.instance.setConfigFlag(NaturesAura.MOD_ID + ":rf_converter", ModConfig.enabledFeatures.rfConverter);
    }

    public static void preInitClient() {
        try {
            bookGuiClass = Class.forName("vazkii.patchouli.client.book.gui.GuiBook");
        } catch (ClassNotFoundException e) {
            NaturesAura.LOGGER.warn("Couldn't find Patchouli book class, not loading special visuals :(");
        }
    }

    public static void addPatchouliMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers) {
        for (int i = 1; i < rawMatchers.length; i += 2) {
            if (rawMatchers[i] instanceof Matcher) {
                Matcher matcher = (Matcher) rawMatchers[i];
                Matcher.ICheck check = matcher.getCheck();
                if (check == null)
                    rawMatchers[i] = PatchouliAPI.instance.anyMatcher();
                else
                    rawMatchers[i] = PatchouliAPI.instance.predicateMatcher(matcher.getDefaultState(),
                            state -> check.matches(null, null, null, null, state, (char) 0));
            }
        }
        PatchouliAPI.instance.registerMultiblock(name, PatchouliAPI.instance.makeMultiblock(pattern, rawMatchers));
    }
}
