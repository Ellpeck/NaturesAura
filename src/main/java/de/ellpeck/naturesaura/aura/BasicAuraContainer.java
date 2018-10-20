package de.ellpeck.naturesaura.aura;

import net.minecraft.nbt.NBTTagCompound;

public class BasicAuraContainer implements IAuraContainer {

    protected final int maxAura;
    protected final boolean artificial;
    protected int aura;

    public BasicAuraContainer(int maxAura, boolean artificial) {
        this.maxAura = maxAura;
        this.artificial = artificial;
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
    public boolean isArtificial() {
        return this.artificial;
    }

    public void writeNBT(NBTTagCompound compound) {
        compound.setInteger("aura", this.aura);
    }

    public void readNBT(NBTTagCompound compound) {
        this.aura = compound.getInteger("aura");
    }
}
