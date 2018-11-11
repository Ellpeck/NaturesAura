package de.ellpeck.naturesaura.api.aura.container;

import de.ellpeck.naturesaura.api.aura.AuraType;
import de.ellpeck.naturesaura.api.aura.chunk.ISpotDrainable;

public class NaturalAuraContainer extends BasicAuraContainer implements ISpotDrainable {

    private final int drainAmount;

    public NaturalAuraContainer(AuraType type, int aura, int drainAmount) {
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
