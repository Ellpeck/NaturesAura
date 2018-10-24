package de.ellpeck.naturesaura.aura.container;

public interface IAuraContainer {
    int storeAura(int amountToStore, boolean simulate);

    int drainAura(int amountToDrain, boolean simulate);

    int getStoredAura();

    int getMaxAura();

    int getAuraColor();
}
