package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.misc.ItemTagProvider;

public interface ICompat {

    void preInit();

    void preInitClient();

    void postInit();

    void addItemTags(ItemTagProvider provider);
}
