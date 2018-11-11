package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;

public interface ISpotDrainable extends IAuraContainer {

    int drainAuraPassively(int amountToDrain, boolean simulate);

}
