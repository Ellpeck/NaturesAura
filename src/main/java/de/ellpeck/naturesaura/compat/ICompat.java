package de.ellpeck.naturesaura.compat;

import de.ellpeck.naturesaura.data.ItemTagProvider;

public interface ICompat {

    void setup();

    void setupClient();

    void addItemTags(ItemTagProvider provider);
}
