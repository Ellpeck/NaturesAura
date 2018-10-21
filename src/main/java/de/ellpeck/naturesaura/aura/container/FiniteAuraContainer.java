package de.ellpeck.naturesaura.aura.container;

public class FiniteAuraContainer extends BasicAuraContainer {

    public FiniteAuraContainer(int aura, boolean artificial) {
        super(aura, artificial);
        this.aura = aura;
    }

    @Override
    public int storeAura(int amountToStore, boolean simulate) {
        return 0;
    }
}
