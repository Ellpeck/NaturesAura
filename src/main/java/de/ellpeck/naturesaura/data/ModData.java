package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.gen.ModFeatures;
import net.minecraft.core.Cloner;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ModData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var lookup = event.getLookupProvider();
        var existing = event.getExistingFileHelper();

        Compat.gatherData(event);

        var blockTags = new BlockTagProvider(out, lookup, existing);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(out, lookup, blockTags.contentsGetter(), existing));
        gen.addProvider(event.includeServer(), new LootTableProvider(out, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider::new, LootContextParamSets.BLOCK)), lookup));
        gen.addProvider(event.includeServer(), new BlockStateGenerator(out, existing));
        gen.addProvider(event.includeServer(), new ItemModelGenerator(out, existing));
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(out, CompletableFuture.supplyAsync(ModData::getProvider), Set.of(NaturesAura.MOD_ID)));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static RegistrySetBuilder.PatchedRegistries getProvider() {
        final var registryBuilder = new RegistrySetBuilder();
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModFeatures.Configured::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, ModFeatures.Placed::bootstrap);
        registryBuilder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifiers::bootstrap);
        // We need the BIOME registry to be present, so we can use a biome tag, doesn't matter that it's empty
        registryBuilder.add(Registries.BIOME, context -> {
        });
        var factory = new Cloner.Factory();
        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().forEach(data -> data.runWithArguments(factory::addCodec));
        return registryBuilder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), VanillaRegistries.createLookup(), factory);
    }

}
