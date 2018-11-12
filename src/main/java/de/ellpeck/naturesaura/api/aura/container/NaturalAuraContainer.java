package de.ellpeck.naturesaura.api.aura.container;

import de.ellpeck.naturesaura.api.aura.chunk.ISpotDrainable;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;

public class NaturalAuraContainer extends BasicAuraContainer implements ISpotDrainable {

    private final int drainAmount;

    public NaturalAuraContainer(IAuraType type, int aura, int drainAmount) {
        super(type, aura);
        this.aura = aura;
        this.drainAmount = drainAmount;
    }

    @Override
    public int storeAura(int amountToStore, boolean simulate) {
        return 0;
    }

    @Override
    public int drainAuraPassively(int amountToDrain, boolean simulate) {
        return this.drainAura(Math.min(this.drainAmount, amountToDrain), simulate);
    }
}
