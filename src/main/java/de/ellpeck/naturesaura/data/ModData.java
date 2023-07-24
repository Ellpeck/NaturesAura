package de.ellpeck.naturesaura.data;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var lookup = event.getLookupProvider();
        var existing = event.getExistingFileHelper();

        var blockTags = new BlockTagProvider(out, lookup, existing);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(out, lookup, blockTags.contentsGetter(), existing));
        gen.addProvider(event.includeServer(), new LootTableProvider(out, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider::new, LootContextParamSets.BLOCK))));
        gen.addProvider(event.includeServer(), new BlockStateGenerator(out, existing));
        gen.addProvider(event.includeServer(), new ItemModelGenerator(out, existing));
    }
}
