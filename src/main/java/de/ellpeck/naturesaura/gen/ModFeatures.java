package de.ellpeck.naturesaura.gen;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
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

        public static final ConfiguredFeature<TreeConfiguration, ?> ANCIENT_TREE = ModFeatures.ANCIENT_TREE.configured(new TreeConfiguration.TreeConfigurationBuilder(null, null, null, null, null).build());
        public static final ConfiguredFeature<NoneFeatureConfiguration, ?> AURA_BLOOM = ModFeatures.AURA_BLOOM.configured(FeatureConfiguration.NONE);
        public static final ConfiguredFeature<NoneFeatureConfiguration, ?> AURA_CACTUS = ModFeatures.AURA_CACTUS.configured(FeatureConfiguration.NONE);
        public static final ConfiguredFeature<NoneFeatureConfiguration, ?> WARPED_AURA_MUSHROOM = ModFeatures.WARPED_AURA_MUSHROOM.configured(FeatureConfiguration.NONE);
        public static final ConfiguredFeature<NoneFeatureConfiguration, ?> CRIMSON_AURA_MUSHROOM = ModFeatures.CRIMSON_AURA_MUSHROOM.configured(FeatureConfiguration.NONE);
        public static final ConfiguredFeature<NoneFeatureConfiguration, ?> AURA_MUSHROOM = ModFeatures.AURA_MUSHROOM.configured(FeatureConfiguration.NONE);

    }

    public static final class Placed {

        public static final PlacedFeature AURA_BLOOM = ModFeatures.Configured.AURA_BLOOM.placed();
        public static final PlacedFeature AURA_CACTUS = ModFeatures.Configured.AURA_CACTUS.placed();
        public static final PlacedFeature WARPED_AURA_MUSHROOM = ModFeatures.Configured.WARPED_AURA_MUSHROOM.placed();
        public static final PlacedFeature CRIMSON_AURA_MUSHROOM = ModFeatures.Configured.CRIMSON_AURA_MUSHROOM.placed();
        public static final PlacedFeature AURA_MUSHROOM = ModFeatures.Configured.AURA_MUSHROOM.placed();

    }
}
