package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.BasicAuraType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.WeightedOre;
import de.ellpeck.naturesaura.chunk.effect.OreSpawnEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ModConfig {

    public static ModConfig instance;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> additionalBotanistPickaxeConversions;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> auraTypeOverrides;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> additionalOres;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> oreExceptions;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> additionalProjectiles;
    public ForgeConfigSpec.ConfigValue<Integer> fieldCreatorRange;
    public ForgeConfigSpec.ConfigValue<Double> auraToRFRatio;

    public ForgeConfigSpec.ConfigValue<Boolean> rfConverter;
    public ForgeConfigSpec.ConfigValue<Boolean> chunkLoader;
    public ForgeConfigSpec.ConfigValue<Boolean> grassDieEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> plantBoostEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> cacheRechargeEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> explosionEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> breathlessEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> animalEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> oreEffect;
    public ForgeConfigSpec.ConfigValue<Boolean> auraBlooms;
    public ForgeConfigSpec.ConfigValue<Boolean> netherGrassEffect;

    public ForgeConfigSpec.ConfigValue<Double> particleAmount;
    public ForgeConfigSpec.ConfigValue<Boolean> respectVanillaParticleSettings;
    public ForgeConfigSpec.ConfigValue<Double> excessParticleAmount;
    public ForgeConfigSpec.ConfigValue<Integer> auraBarLocation;
    public ForgeConfigSpec.ConfigValue<Integer> cacheBarLocation;
    public ForgeConfigSpec.ConfigValue<Boolean> debugText;
    public ForgeConfigSpec.ConfigValue<Boolean> debugWorld;
    public ForgeConfigSpec.ConfigValue<Boolean> renderItemsOnPlayer;

    public ModConfig(ForgeConfigSpec.Builder builder) {
        builder.push("general");
        this.additionalBotanistPickaxeConversions = builder
                .comment("Additional conversion recipes for the Botanist's Pickaxe right click function. Each entry needs to be formatted as modid:input_block[prop1=value1,...]->modid:output_block[prop1=value1,...] where block state properties are optional")
                .translation("config." + NaturesAura.MOD_ID + ".additionalBotanistPickaxeConversions")
                .defineList("additionalBotanistPickaxeConversions", Collections.emptyList(), s -> true);
        this.auraTypeOverrides = builder
                .comment("Additional dimensions that map to Aura types that should be present in them. This is useful if you have a modpack with custom dimensions that should have Aura act similarly to an existing dimension in them. Each entry needs to be formatted as dimension_name->aura_type, where aura_type can be any of naturesaura:overworld, naturesaura:nether and naturesaura:end.")
                .translation("config." + NaturesAura.MOD_ID + ".auraTypeOverrides")
                .defineList("auraTypeOverrides", Collections.emptyList(), s -> true);
        this.additionalOres = builder
                .comment("Additional blocks that are recognized as generatable ores for the passive ore generation effect. Each entry needs to be formatted as tag_name->oreWeight->dimension where a higher weight makes the ore more likely to spawn with 5000 being the weight of coal, the default ore with the highest weight, and dimension being any of overworld and nether")
                .translation("config." + NaturesAura.MOD_ID + ".additionalOres")
                .defineList("additionalOres", Collections.emptyList(), s -> true);
        this.oreExceptions = builder
                .comment("Blocks that are exempt from being recognized as generatable ores for the passive ore generation effect. Each entry needs to be formatted as modid:block[prop1=value1,...] where block state properties are optional")
                .translation("config." + NaturesAura.MOD_ID + ".oreExceptions")
                .defineList("oreExceptions", Collections.emptyList(), s -> true);
        this.additionalProjectiles = builder
                .comment("Additional projectile types that are allowed to be consumed by the projectile generator. Each entry needs to be formatted as entity_registry_name->aura_amount")
                .translation("config." + NaturesAura.MOD_ID + ".additionalProjectiles")
                .defineList("additionalProjectiles", Collections.emptyList(), s -> true);
        this.fieldCreatorRange = builder
                .comment("The amount of blocks that can be between two Aura Field Creators for them to be connectable and work together")
                .translation("config." + NaturesAura.MOD_ID + ".fieldCreatorRange")
                .define("fieldCreatorRange", 10);
        this.auraToRFRatio = builder
                .comment("The Aura to RF ratio used by the RF converter, read as aura*ratio = rf")
                .translation("config." + NaturesAura.MOD_ID + ".auraToRFRatio")
                .define("auraToRFRatio", 0.05);
        builder.pop();

        builder.push("features");
        this.rfConverter = builder
                .comment("If the RF converter block should be enabled")
                .translation("config." + NaturesAura.MOD_ID + ".rfConverter")
                .define("rfConverter", true);
        this.chunkLoader = builder
                .comment("If the chunk loader block should be enabled")
                .translation("config." + NaturesAura.MOD_ID + ".chunkLoader")
                .define("chunkLoader", true);
        this.grassDieEffect = builder
                .comment("If the Aura Imbalance effect of grass and trees dying in the area if the Aura levels are too low should occur")
                .translation("config." + NaturesAura.MOD_ID + ".grassDieEffect")
                .define("grassDieEffect", true);
        this.plantBoostEffect = builder
                .comment("If the Aura Imbalance effect of plant growth being boosted if the Aura levels are high enough should occur")
                .translation("config." + NaturesAura.MOD_ID + ".plantBoostEffect")
                .define("plantBoostEffect", true);
        this.cacheRechargeEffect = builder
                .comment("If the Aura Imbalance effect of aura containers in players' inventories being filled if the Aura levels are high enough should occur")
                .translation("config." + NaturesAura.MOD_ID + ".cacheRechargeEffect")
                .define("cacheRechargeEffect", true);
        this.explosionEffect = builder
                .comment("If the Aura Imbalance effect of explosions happening randomly if Aura levels are too low should occur")
                .translation("config." + NaturesAura.MOD_ID + ".explosionEffect")
                .define("explosionEffect", true);
        this.breathlessEffect = builder
                .comment("If the Aura Imbalance effect of breathlessness if Aura levels are too low should occur")
                .translation("config." + NaturesAura.MOD_ID + ".breathlessEffect")
                .define("breathlessEffect", true);
        this.animalEffect = builder
                .comment("If the Aura Imbalance effect of farm animals being affected in positive ways if Aura levels are too high should occur")
                .translation("config." + NaturesAura.MOD_ID + ".animalEffect")
                .define("animalEffect", true);
        this.oreEffect = builder
                .comment("If the Aura Imbalance effect of ores spawning in the area if Aura levels are too high should occur")
                .translation("config." + NaturesAura.MOD_ID + ".oreEffect")
                .define("oreEffect", true);
        this.auraBlooms = builder
                .comment("If Aura Blooms and Aura Cacti should generate in the world")
                .translation("config." + NaturesAura.MOD_ID + ".auraBlooms")
                .define("auraBlooms", true);
        this.netherGrassEffect = builder
                .comment("If the Aura Imbalance effect of grass growing on netherrack if the Aura levels are high enough should occur")
                .translation("config." + NaturesAura.MOD_ID + ".netherGrassEffect")
                .define("netherGrassEffect", true);
        builder.pop();

        builder.push("client");
        this.particleAmount = builder
                .comment("The percentage of particles that should be displayed, where 1 is 100% and 0 is 0%")
                .translation("config." + NaturesAura.MOD_ID + ".particleAmount")
                .defineInRange("particleAmount", 1D, 0, 1);
        this.respectVanillaParticleSettings = builder
                .comment("If particle spawning should respect the particle setting in Minecraft's video settings screen")
                .translation("config." + NaturesAura.MOD_ID + ".respectVanillaParticleSettings")
                .define("respectVanillaParticleSettings", false);
        this.excessParticleAmount = builder
                .comment("The percentage of particles that should spawn when there is an excess amount of Aura in the environment, where 1 is 100% and 0 is 0%")
                .translation("config." + NaturesAura.MOD_ID + ".excessParticleAmount")
                .define("excessParticleAmount", 1D);
        this.auraBarLocation = builder
                .comment("The location of the aura bar, where 0 is top left, 1 is top right, 2 is bottom left and 3 is bottom right")
                .translation("config." + NaturesAura.MOD_ID + ".auraBarLocation")
                .defineInRange("auraBarLocation", 0, 0, 3);
        this.cacheBarLocation = builder
                .comment("The location of the aura cache bar, where 0 is to the left of the hotbar and 1 is to the right of the hotbar")
                .translation("config." + NaturesAura.MOD_ID + ".cacheBarLocation")
                .defineInRange("cacheBarLocation", 0, 0, 1);
        this.debugText = builder
                .comment("If debug information about Aura around the player should be displayed in the F3 debug menu if the player is in creative mode")
                .translation("config." + NaturesAura.MOD_ID + ".debugText")
                .define("debugText", true);
        this.debugWorld = builder
                .comment("If, when the F3 debug menu is open and the player is in creative mode, every Aura spot should be highlighted in the world for debug purposes")
                .translation("config." + NaturesAura.MOD_ID + ".debugWorld")
                .define("debugWorld", false);
        this.renderItemsOnPlayer = builder
                .comment("If certain equippable items, like the Environmental Eye, should be rendered on the player")
                .translation("config." + NaturesAura.MOD_ID + ".renderItemsOnPlayer")
                .define("renderItemsOnPlayer", true);
        builder.pop();
    }

    public void apply() {
        if (!this.grassDieEffect.get() && !this.explosionEffect.get() && !this.breathlessEffect.get()) {
            throw new IllegalStateException("Nature's Aura has detected that all negative Aura Imbalance effects are disabled in the config file. This is disallowed behavior. Please enable at least one negative effect.");
        }

        try {
            for (String s : this.additionalBotanistPickaxeConversions.get()) {
                String[] split = s.split("->");
                NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                        Objects.requireNonNull(Helper.getStateFromString(split[0]), "state1"),
                        Objects.requireNonNull(Helper.getStateFromString(split[1]), "state2"));
            }
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing additionalBotanistPickaxeConversions", e);
        }

        try {
            for (String s : this.auraTypeOverrides.get()) {
                String[] split = s.split("->");
                ResourceLocation dim = new ResourceLocation(split[0]);
                BasicAuraType type = Objects.requireNonNull((BasicAuraType) NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(split[1])), "type");
                type.addDimensionType(dim);
            }
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing auraTypeOverrides", e);
        }

        try {
            for (String s : this.additionalOres.get()) {
                String[] split = s.split("->");
                WeightedOre ore = new WeightedOre(new ResourceLocation(split[0]), Integer.parseInt(split[1]));
                String dimension = split[2];
                if ("nether".equalsIgnoreCase(dimension)) {
                    NaturesAuraAPI.NETHER_ORES.add(ore);
                } else if ("overworld".equalsIgnoreCase(dimension)) {
                    NaturesAuraAPI.OVERWORLD_ORES.add(ore);
                } else {
                    throw new IllegalArgumentException(dimension);
                }
            }
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing additionalOres", e);
        }

        try {
            for (String s : this.oreExceptions.get())
                OreSpawnEffect.SPAWN_EXCEPTIONS.add(Objects.requireNonNull(Helper.getStateFromString(s)));
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing oreExceptions", e);
        }

        try {
            for (String s : this.additionalProjectiles.get()) {
                String[] split = s.split("->");
                ResourceLocation name = new ResourceLocation(split[0]);
                EntityType<?> type = Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(name));
                int amount = Integer.parseInt(split[1]);
                NaturesAuraAPI.PROJECTILE_GENERATIONS.put(type, amount);
            }
        } catch (Exception e) {
            NaturesAura.LOGGER.warn("Error parsing additionalProjectiles", e);
        }
    }
}
