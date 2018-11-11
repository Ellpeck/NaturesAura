package de.ellpeck.naturesaura.api.aura.container;

import de.ellpeck.naturesaura.api.aura.AuraType;
import net.minecraft.nbt.NBTTagCompound;

public class BasicAuraContainer implements IAuraContainer {

    protected final AuraType type;
    protected final int maxAura;
    protected int aura;

    public BasicAuraContainer(AuraType type, int maxAura) {
        this.type = type;
        this.maxAura = maxAura;
    }

    @Override
    public int storeAura(int amountToStore, boolean simulate) {
        int actual = Math.min(amountToStore, this.maxAura - this.aura);
        if (!simulate) {
            this.aura += actual;
        }
        return actual;
    }

    @Override
    public int drainAura(int amountToDrain, boolean simulate) {
        int actual = Math.min(amountToDrain, this.aura);
        if (!simulate) {
            this.aura -= actual;
        }
        return actual;
    }

    @Override
    public int getStoredAura() {
        return this.aura;
    }

    @Override
    public int getMaxAura() {
        return this.maxAura;
    }

    @Override
    public int getAuraColor() {
        return 0x1E891E;
    }

    @Override
    public boolean isAcceptableType(AuraType type) {
        return this.type == null || this.type == type;
    }

    public void writeNBT(NBTTagCompound compound) {
        compound.setInteger("aura", this.aura);
    }

    public void readNBT(NBTTagCompound compound) {
        this.aura = compound.getInteger("aura");
    }
}
