package de.ellpeck.naturesaura.gen;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

@SuppressWarnings({"NonConstantFieldWithUpperCaseName", "FieldNamingConvention"})
public final class ModFeatures {

    public static Feature<TreeConfiguration> ANCIENT_TREE;
    public static Feature<NoneFeatureConfiguration> NETHER_WART_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> AURA_BLOOM;
    public static Feature<NoneFeatureConfiguration> AURA_CACTUS;
    public static Feature<NoneFeatureConfiguration> WARPED_AURA_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> CRIMSON_AURA_MUSHROOM;
    public static Feature<NoneFeatureConfiguration> AURA_MUSHROOM;

    public static final class Configured {

        public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_TREE = FeatureUtils.createKey(NaturesAura.MOD_ID + ":ancient_tree");
        public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_WART_MUSHROOM = FeatureUtils.createKey(NaturesAura.MOD_ID + ":nether_wart_mushroom");
        public static final ResourceKey<ConfiguredFeature<?, ?>> AURA_BLOOM = FeatureUtils.createKey(NaturesAura.MOD_ID + ":aura_bloom");
        public static final ResourceKey<ConfiguredFeature<?, ?>> AURA_CACTUS = FeatureUtils.createKey(NaturesAura.MOD_ID + ":aura_cactus");
        public static final ResourceKey<ConfiguredFeature<?, ?>> WARPED_AURA_MUSHROOM = FeatureUtils.createKey(NaturesAura.MOD_ID + ":warped_aura_mushroom");
        public static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_AURA_MUSHROOM = FeatureUtils.createKey(NaturesAura.MOD_ID + ":crimson_aura_mushroom");
        public static final ResourceKey<ConfiguredFeature<?, ?>> AURA_MUSHROOM = FeatureUtils.createKey(NaturesAura.MOD_ID + ":aura_mushroom");

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            FeatureUtils.register(context, ModFeatures.Configured.AURA_BLOOM, ModFeatures.AURA_BLOOM, NoneFeatureConfiguration.INSTANCE);
            FeatureUtils.register(context, ModFeatures.Configured.AURA_CACTUS, ModFeatures.AURA_CACTUS, NoneFeatureConfiguration.INSTANCE);
            FeatureUtils.register(context, ModFeatures.Configured.WARPED_AURA_MUSHROOM, ModFeatures.WARPED_AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
            FeatureUtils.register(context, ModFeatures.Configured.CRIMSON_AURA_MUSHROOM, ModFeatures.CRIMSON_AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
            FeatureUtils.register(context, ModFeatures.Configured.AURA_MUSHROOM, ModFeatures.AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
            FeatureUtils.register(context, ModFeatures.Configured.ANCIENT_TREE, ModFeatures.ANCIENT_TREE, new TreeConfiguration.TreeConfigurationBuilder(null, null, null, null, null).build());
            FeatureUtils.register(context, ModFeatures.Configured.NETHER_WART_MUSHROOM, ModFeatures.NETHER_WART_MUSHROOM, NoneFeatureConfiguration.INSTANCE);

        }

    }

    public static final class Placed {

        public static final ResourceKey<PlacedFeature> AURA_BLOOM = PlacementUtils.createKey(NaturesAura.MOD_ID + ":aura_bloom");
        public static final ResourceKey<PlacedFeature> AURA_CACTUS = PlacementUtils.createKey(NaturesAura.MOD_ID + ":aura_cactus");
        public static final ResourceKey<PlacedFeature> WARPED_AURA_MUSHROOM = PlacementUtils.createKey(NaturesAura.MOD_ID + ":warped_aura_mushroom");
        public static final ResourceKey<PlacedFeature> CRIMSON_AURA_MUSHROOM = PlacementUtils.createKey(NaturesAura.MOD_ID + ":crimson_aura_mushroom");
        public static final ResourceKey<PlacedFeature> AURA_MUSHROOM = PlacementUtils.createKey(NaturesAura.MOD_ID + ":aura_mushroom");

        public static void bootstrap(BootstrapContext<PlacedFeature> context) {
            var holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);

            PlacementUtils.register(context, Placed.AURA_BLOOM, holdergetter.getOrThrow(ModFeatures.Configured.AURA_BLOOM));
            PlacementUtils.register(context, Placed.AURA_CACTUS, holdergetter.getOrThrow(ModFeatures.Configured.AURA_CACTUS));
            PlacementUtils.register(context, Placed.WARPED_AURA_MUSHROOM, holdergetter.getOrThrow(ModFeatures.Configured.WARPED_AURA_MUSHROOM));
            PlacementUtils.register(context, Placed.CRIMSON_AURA_MUSHROOM, holdergetter.getOrThrow(ModFeatures.Configured.CRIMSON_AURA_MUSHROOM));
            PlacementUtils.register(context, Placed.AURA_MUSHROOM, holdergetter.getOrThrow(ModFeatures.Configured.AURA_MUSHROOM));

        }

    }

}
