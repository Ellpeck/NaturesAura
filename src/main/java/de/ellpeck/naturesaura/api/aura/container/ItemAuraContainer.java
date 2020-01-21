package de.ellpeck.naturesaura.api.aura.container;

import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemAuraContainer implements IAuraContainer {

    protected final ItemStack stack;
    protected final IAuraType type;
    protected final int maxAura;

    public ItemAuraContainer(ItemStack stack, IAuraType type, int maxAura) {
        this.stack = stack;
        this.type = type;
        this.maxAura = maxAura;
    }

    @Override
    public int storeAura(int amountToStore, boolean simulate) {
        int aura = this.getStoredAura();
        int actual = Math.min(amountToStore, this.getMaxAura() - aura);
        if (!simulate) {
            this.setAura(aura + actual);
        }
        return actual;
    }

    @Override
    public int drainAura(int amountToDrain, boolean simulate) {
        int aura = this.getStoredAura();
        int actual = Math.min(amountToDrain, aura);
        if (!simulate) {
            this.setAura(aura - actual);
        }
        return actual;
    }

    private void setAura(int amount) {
        if (!this.stack.hasTag()) {
            this.stack.setTag(new CompoundNBT());
        }
        this.stack.getTag().putInt("aura", amount);
    }

    @Override
    public int getStoredAura() {
        if (this.stack.hasTag()) {
            return this.stack.getTag().getInt("aura");
        } else {
            return 0;
        }
    }

    @Override
    public int getMaxAura() {
        return this.maxAura;
    }

    @Override
    public int getAuraColor() {
        return 0x42a6bc;
    }

    @Override
    public boolean isAcceptableType(IAuraType type) {
        return this.type == null || this.type == type;
    }
}
