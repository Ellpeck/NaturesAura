package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = NaturesAura.MOD_ID, category = "")
public final class ModConfig {

    public static General general = new General();
    public static Features enabledFeatures = new Features();
    public static Client client = new Client();

    public static class General {

        @Comment("If using Dragon's Breath in a Brewing Stand should not cause a glass bottle to appear")
        public boolean removeDragonBreathContainerItem = true;

        @Comment("Additional conversion recipes for the Botanist's Pickaxe right click function. Each entry needs to be formatted as modid:input_block[prop1=value1,...]->modid:output_block[prop1=value1,...] where block state properties are optional")
        public String[] additionalBotanistPickaxeConversions = new String[0];

        @Comment("Additional blocks that the Herbivorous Absorber can consume to generate Aura. Each entry needs to be formatted as modid:block[prop1=value1,...] where block state properties are optional")
        public String[] additionalHerbivorousAbsorberFlowers = new String[0];

        @Comment("The amount of blocks that can be between two Aura Field Creators for them to be connectable and work together")
        public int fieldCreatorRange = 8;
    }

    public static class Features {

        @Comment("If the Aura Imbalance effect of grass and trees dying in the area if the Aura levels are too low should occur")
        public boolean grassDieEffect = true;
        @Comment("If the Aura Imbalance effect of plant growth being boosted if the Aura levels are high enough should occur")
        public boolean plantBoostEffect = true;
    }

    public static class Client {

        @Comment("The percentage of particles that should be displayed, where 1 is 100% and 0 is 0%")
        @RangeDouble(min = 0, max = 1)
        public double particleAmount = 1;

        @Comment("If particle spawning should respect the particle setting in Minecraft's video settings screen")
        public boolean respectVanillaParticleSettings = true;
    }

    public static void initOrReload() {
        try {
            for (String s : general.additionalBotanistPickaxeConversions) {
                String[] split = s.split("->");
                NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                        Helper.getStateFromString(split[0]),
                        Helper.getStateFromString(split[1]));
            }
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing additionalBotanistPickaxeConversions", e);
        }

        try {
            for (String s : general.additionalHerbivorousAbsorberFlowers)
                NaturesAuraAPI.FLOWER_GENERATOR_BLOCKS.add(Helper.getStateFromString(s));
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing additionalHerbivorousAbsorberFlowers", e);
        }
    }
}
