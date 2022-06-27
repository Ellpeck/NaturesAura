package de.ellpeck.naturesaura.potion;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PotionImpl extends MobEffect implements IModItem {

    protected final String baseName;

    protected PotionImpl(String baseName, MobEffectCategory type, int liquidColorIn) {
        super(type, liquidColorIn);
        this.baseName = baseName;

        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
