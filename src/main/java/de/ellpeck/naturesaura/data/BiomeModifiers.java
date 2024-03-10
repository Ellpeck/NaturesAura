package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.gen.ModFeatures;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BiomeModifiers {

    public static final ResourceKey<BiomeModifier> AURA_BLOOM = BiomeModifiers.createKey("aura_bloom");
    public static final ResourceKey<BiomeModifier> AURA_CACTUS = BiomeModifiers.createKey("aura_cactus");
    public static final ResourceKey<BiomeModifier> WARPED_AURA_MUSHROOM = BiomeModifiers.createKey("warped_aura_mushroom");
    public static final ResourceKey<BiomeModifier> CRIMSON_AURA_MUSHROOM = BiomeModifiers.createKey("crimson_aura_mushroom");
    public static final ResourceKey<BiomeModifier> AURA_MUSHROOM = BiomeModifiers.createKey("aura_mushroom");

    private static ResourceKey<BiomeModifier> createKey(String id) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(NaturesAura.MOD_ID, id));
    }

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var biomeGetter = context.lookup(Registries.BIOME);
        var placedGetter = context.lookup(Registries.PLACED_FEATURE);

        context.register(BiomeModifiers.AURA_BLOOM, new AddFeaturesBiomeModifier(
                biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_BLOOM)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(BiomeModifiers.AURA_CACTUS, new AddFeaturesBiomeModifier(
                biomeGetter.getOrThrow(Tags.Biomes.IS_SANDY),
                HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_CACTUS)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(BiomeModifiers.AURA_MUSHROOM, new AddFeaturesBiomeModifier(
                biomeGetter.getOrThrow(Tags.Biomes.IS_MUSHROOM),
                HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_MUSHROOM)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(BiomeModifiers.CRIMSON_AURA_MUSHROOM, new AddFeaturesBiomeModifier(
                biomeGetter.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.CRIMSON_AURA_MUSHROOM)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(BiomeModifiers.WARPED_AURA_MUSHROOM, new AddFeaturesBiomeModifier(
                biomeGetter.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.WARPED_AURA_MUSHROOM)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

}
