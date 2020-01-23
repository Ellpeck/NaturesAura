package de.ellpeck.naturesaura.potion;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class PotionImpl extends Effect implements IModItem {

    protected final String baseName;

    protected PotionImpl(String baseName, EffectType type, int liquidColorIn) {
        super(type, liquidColorIn);
        this.baseName = baseName;

        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
