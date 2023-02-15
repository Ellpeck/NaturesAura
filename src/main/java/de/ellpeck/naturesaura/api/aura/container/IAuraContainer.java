package de.ellpeck.naturesaura.api.aura.container;

import de.ellpeck.naturesaura.api.aura.type.IAuraType;

public interface IAuraContainer {

    int storeAura(int amountToStore, boolean simulate);

    int drainAura(int amountToDrain, boolean simulate);

    int getStoredAura();

    int getMaxAura();

    int getAuraColor();

    boolean isAcceptableType(IAuraType type);
}
