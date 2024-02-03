package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.data.ItemTagProvider;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public interface ICompat {

    void setup(FMLCommonSetupEvent event);

    void setupClient();

    void addItemTags(ItemTagProvider provider);
}
