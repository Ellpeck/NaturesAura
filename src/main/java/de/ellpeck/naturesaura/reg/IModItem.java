package de.ellpeck.naturesaura.reg;

import net.minecraftforge.registries.ForgeRegistryEntry;

public interface IModItem {

    String getBaseName();

    default ForgeRegistryEntry<?> getRegistryEntry() {
        return (ForgeRegistryEntry<?>) this;
    }
}
