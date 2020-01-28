package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.data.ItemTagProvider;

public interface ICompat {

    void preInit();

    void preInitClient();

    void postInit();

    void addItemTags(ItemTagProvider provider);
}
