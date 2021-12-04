package de.ellpeck.naturesaura.gen;

import net.minecraft.level.gen.feature.*;
import net.minecraft.level.gen.placement.IPlacementConfig;
import net.minecraft.level.gen.placement.Placement;

@SuppressWarnings("FieldNamingConvention")
public final class ModFeatures {

    public static Feature<BaseTreeFeatureConfig> ANCIENT_TREE;
    public static Feature<NoFeatureConfig> NETHER_WART_MUSHROOM;
    public static Feature<NoFeatureConfig> AURA_BLOOM;
    public static Feature<NoFeatureConfig> AURA_CACTUS;
    public static Feature<NoFeatureConfig> WARPED_AURA_MUSHROOM;
    public static Feature<NoFeatureConfig> CRIMSON_AURA_MUSHROOM;
    public static Feature<NoFeatureConfig> AURA_MUSHROOM;

    public static final class Configured {

        public static final ConfiguredFeature AURA_BLOOM = ModFeatures.AURA_BLOOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
        public static final ConfiguredFeature AURA_CACTUS = ModFeatures.AURA_CACTUS.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
        public static final ConfiguredFeature CRIMSON_AURA_MUSHROOM = ModFeatures.CRIMSON_AURA_MUSHROOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
        public static final ConfiguredFeature WARPED_AURA_MUSHROOM = ModFeatures.WARPED_AURA_MUSHROOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
        public static final ConfiguredFeature AURA_MUSHROOM = ModFeatures.AURA_MUSHROOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));

    }
}
