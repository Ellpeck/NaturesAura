package de.ellpeck.naturesaura.data;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var ex = event.getExistingFileHelper();
        var blockTags = new BlockTagProvider(generator, ex);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTagProvider(generator, blockTags, ex));
        generator.addProvider(event.includeServer(), new BlockLootProvider(generator));
        generator.addProvider(event.includeServer(), new BlockStateGenerator(generator, ex));
        generator.addProvider(event.includeServer(), new ItemModelGenerator(generator, ex));
    }
}
