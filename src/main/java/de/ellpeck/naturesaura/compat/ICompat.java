package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.data.ItemTagProvider;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public interface ICompat {

    default void setup(FMLCommonSetupEvent event) {}

    default void setupClient() {}

    default void gatherData(GatherDataEvent event) {}

    default void addItemTags(ItemTagProvider provider) {}

    default void addCapabilities(RegisterCapabilitiesEvent event) {}

}
