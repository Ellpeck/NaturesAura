package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.BasicAuraType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.recipes.WeightedOre;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = NaturesAura.MOD_ID, category = "")
public final class ModConfig {

    public static General general = new General();
    public static Features enabledFeatures = new Features();
    public static Client client = new Client();

    public static class General {

        @Comment("Additional conversion recipes for the Botanist's Pickaxe right click function. Each entry needs to be formatted as modid:input_block[prop1=value1,...]->modid:output_block[prop1=value1,...] where block state properties are optional")
        public String[] additionalBotanistPickaxeConversions = new String[0];

        @Comment("Additional blocks that several mechanics identify as flowers. Each entry needs to be formatted as modid:block[prop1=value1,...] where block state properties are optional")
        public String[] additionalFlowers = new String[0];

        @Comment("Additional dimensions that map to Aura types that should be present in them. This is useful if you have a modpack with custom dimensions that should have Aura act similarly to an existing dimension in them. Each entry needs to be formatted as dimension_name->aura_type, where aura_type can be any of naturesaura:overworld, naturesaura:nether and naturesaura:end.")
        public String[] auraTypeOverrides = new String[0];

        @Comment("Additional blocks that are recognized as generatable ores for the passive ore generation effect. Each entry needs to be formatted as oredictEntry:oreWeight:dimension where a higher weight makes the ore more likely to spawn with 5000 being the weight of coal, the default ore with the highest weight, and dimension being any of overworld and nether")
        public String[] additionalOres = new String[0];

        @Comment("Additional projectile types that are allowed to be consumed by the projectile generator. Each entry needs to be formatted as entity_registry_name->aura_amount")
        public String[] additionalProjectiles = new String[0];

        @Comment("The amount of blocks that can be between two Aura Field Creators for them to be connectable and work together")
        public int fieldCreatorRange = 10;

        @Comment("The Aura to RF ratio used by the RF converter, read as aura*ratio = rf")
        public float auraToRFRatio = 0.05F;
    }

    public static class Features {

        @Comment("If using Dragon's Breath in a Brewing Stand should not cause a glass bottle to appear")
        public boolean removeDragonBreathContainerItem = true;
        @Comment("If the RF converter block should be enabled")
        public boolean rfConverter = true;
        @Comment("If the chunk loader block should be enabled")
        public boolean chunkLoader = true;

        @Comment("If the Aura Imbalance effect of grass and trees dying in the area if the Aura levels are too low should occur")
        public boolean grassDieEffect = true;
        @Comment("If the Aura Imbalance effect of plant growth being boosted if the Aura levels are high enough should occur")
        public boolean plantBoostEffect = true;
        @Comment("If the Aura Imbalance effect of aura containers in players' inventories being filled if the Aura levels are high enough should occur")
        public boolean cacheRechargeEffect = true;
        @Comment("If the Aura Imbalance effect of explosions happening randomly if Aura levels are too low should occur")
        public boolean explosionEffect = true;
        @Comment("If the Aura Imbalance effect of breathlessness if Aura levels are too low should occur")
        public boolean breathlessEffect = true;
        @Comment("If the Aura Imbalance effect of farm animals being affected in positive ways if Aura levels are too high should occur")
        public boolean animalEffect = true;
        @Comment("If the Aura Imbalance effect of ores spawning in the area if Aura levels are too high should occur")
        public boolean oreEffect = true;
    }

    public static class Client {

        @Comment("The percentage of particles that should be displayed, where 1 is 100% and 0 is 0%")
        @RangeDouble(min = 0, max = 1)
        public double particleAmount = 1;
        @Comment("If particle spawning should respect the particle setting in Minecraft's video settings screen")
        public boolean respectVanillaParticleSettings = true;
        @Comment("The percentage of particles that should spawn when there is an excess amount of Aura in the environment, where 1 is 100% and 0 is 0%")
        public double excessParticleAmount = 1;
        @Comment("The location of the aura bar, where 0 is top left, 1 is top right, 2 is bottom left and 3 is bottom right")
        @Config.RangeInt(min = 0, max = 3)
        public int auraBarLocation = 0;

        @Comment("If debug information about Aura around the player should be displayed in the F3 debug menu if the player is in creative mode")
        public boolean debugText = true;
        @Comment("If, when the F3 debug menu is open and the player is in creative mode, every Aura spot should be highlighted in the world for debug purposes")
        public boolean debugWorld = false;
    }

    public static void initOrReload(boolean reload) {
        if (!reload) {
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
                for (String s : general.additionalFlowers)
                    NaturesAuraAPI.FLOWERS.add(Helper.getStateFromString(s));
            } catch (Exception e) {
                NaturesAura.LOGGER.warn("Error parsing additionalFlowers", e);
            }

            try {
                for (String s : general.auraTypeOverrides) {
                    String[] split = s.split("->");
                    IAuraType type = NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(split[1]));
                    if (type instanceof BasicAuraType)
                        ((BasicAuraType) type).addDimensionType(DimensionType.byName(split[0]));
                }
            } catch (Exception e) {
                NaturesAura.LOGGER.warn("Error parsing auraTypeOverrides", e);
            }

            try {
                for (String s : general.additionalOres) {
                    String[] split = s.split(":");
                    WeightedOre ore = new WeightedOre(split[0], Integer.parseInt(split[1]));
                    String dimension = split[2];
                    if ("nether".equalsIgnoreCase(dimension))
                        NaturesAuraAPI.NETHER_ORES.add(ore);
                    else
                        NaturesAuraAPI.OVERWORLD_ORES.add(ore);
                }
            } catch (Exception e) {
                NaturesAura.LOGGER.warn("Error parsing additionalOres", e);
            }

            try {
                for (String s : general.additionalProjectiles) {
                    String[] split = s.split("->");
                    ResourceLocation name = new ResourceLocation(split[0]);
                    int amount = Integer.parseInt(split[1]);
                    NaturesAuraAPI.PROJECTILE_GENERATIONS.put(name, amount);
                }
            } catch (Exception e) {
                NaturesAura.LOGGER.warn("Error parsing additionalProjectiles", e);
            }
        }
    }
}
