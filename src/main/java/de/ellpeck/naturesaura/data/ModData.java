package de.ellpeck.naturesaura.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper ex = event.getExistingFileHelper();
        generator.addProvider(new BlockLootProvider(generator));
        generator.addProvider(new BlockTagProvider(generator));
        generator.addProvider(new ItemTagProvider(generator));
        generator.addProvider(new BlockStateGenerator(generator, ex));
        generator.addProvider(new ItemModelGenerator(generator, ex));
    }
}
