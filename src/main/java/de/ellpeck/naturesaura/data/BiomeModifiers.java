package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.gen.ModFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeModifiers {
	public static ResourceKey<BiomeModifier> AURA_BLOOM = createKey("aura_bloom");
	public static ResourceKey<BiomeModifier> AURA_CACTUS = createKey("aura_cactus");
	public static ResourceKey<BiomeModifier> WARPED_AURA_MUSHROOM = createKey("warped_aura_mushroom");
	public static ResourceKey<BiomeModifier> CRIMSON_AURA_MUSHROOM = createKey("crimson_aura_mushroom");
	public static ResourceKey<BiomeModifier> AURA_MUSHROOM = createKey("aura_mushroom");

	private static ResourceKey<BiomeModifier> createKey(String id) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(NaturesAura.MOD_ID, id));
	}

	public static void bootstrap(BootstapContext<BiomeModifier> context) {
		HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> placedGetter = context.lookup(Registries.PLACED_FEATURE);

		context.register(AURA_BLOOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
				HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_BLOOM)),
				GenerationStep.Decoration.VEGETAL_DECORATION));

		context.register(AURA_CACTUS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomeGetter.getOrThrow(Tags.Biomes.IS_SANDY),
				HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_CACTUS)),
				GenerationStep.Decoration.VEGETAL_DECORATION));

		context.register(AURA_MUSHROOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomeGetter.getOrThrow(Tags.Biomes.IS_MUSHROOM),
				HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.AURA_MUSHROOM)),
				GenerationStep.Decoration.VEGETAL_DECORATION));

		context.register(CRIMSON_AURA_MUSHROOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomeGetter.getOrThrow(BiomeTags.IS_NETHER),
				HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.CRIMSON_AURA_MUSHROOM)),
				GenerationStep.Decoration.VEGETAL_DECORATION));

		context.register(WARPED_AURA_MUSHROOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
				biomeGetter.getOrThrow(BiomeTags.IS_NETHER),
				HolderSet.direct(placedGetter.getOrThrow(ModFeatures.Placed.WARPED_AURA_MUSHROOM)),
				GenerationStep.Decoration.VEGETAL_DECORATION));
	}
}
