package de.ellpeck.naturesaura.gen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@SuppressWarnings("FieldNamingConvention")
public final class ModFeatures {

    public static Feature<TreeConfiguration> ANCIENT_TREE;
    public static Feature<NoneFeatureConfiguration> NETHER_WART_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> AURA_BLOOM;
    public static Feature<NoneFeatureConfiguration> AURA_CACTUS;
    public static Feature<NoneFeatureConfiguration> WARPED_AURA_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> CRIMSON_AURA_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> AURA_MUSHROOM;

    public static final class Configured {

        public static Holder<ConfiguredFeature<TreeConfiguration, ?>> ANCIENT_TREE;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> NETHER_WART_MUSHROOM;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> AURA_BLOOM;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> AURA_CACTUS;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> WARPED_AURA_MUSHROOM;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> CRIMSON_AURA_MUSHROOM;
        public static Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> AURA_MUSHROOM;

    }

    public static final class Placed {

        public static Holder<PlacedFeature> AURA_BLOOM;
        public static Holder<PlacedFeature> AURA_CACTUS;
        public static Holder<PlacedFeature> WARPED_AURA_MUSHROOM;
        public static Holder<PlacedFeature> CRIMSON_AURA_MUSHROOM;
        public static Holder<PlacedFeature> AURA_MUSHROOM;

    }
}
