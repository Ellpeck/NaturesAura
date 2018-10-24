package de.ellpeck.naturesaura.aura.chunk;

import de.ellpeck.naturesaura.aura.container.IAuraContainer;

public interface ISpotDrainable extends IAuraContainer {

    int drainAuraPassively(int amountToDrain, boolean simulate);

}
