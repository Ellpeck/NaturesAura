package de.ellpeck.naturesaura.api.aura.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

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
        var aura = this.getStoredAura();
        var actual = Math.min(amountToStore, this.getMaxAura() - aura);
        if (!simulate) {
            this.setAura(aura + actual);
        }
        return actual;
    }

    @Override
    public int drainAura(int amountToDrain, boolean simulate) {
        var aura = this.getStoredAura();
        var actual = Math.min(amountToDrain, aura);
        if (!simulate) {
            this.setAura(aura - actual);
        }
        return actual;
    }

    private void setAura(int amount) {
        this.stack.set(Data.TYPE, new Data(amount));
    }

    @Override
    public int getStoredAura() {
        if (this.stack.has(Data.TYPE)) {
            return this.stack.get(Data.TYPE).auraAmount;
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

    public record Data(int auraAmount) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("aura_amount").forGetter(d -> d.auraAmount)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
