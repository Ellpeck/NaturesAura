package de.ellpeck.naturesaura.api.misc;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;

public class WeightedOre extends WeightedRandom.Item {

    public final ResourceLocation tag;

    public WeightedOre(ResourceLocation tag, int weight) {
        super(weight);
        this.tag = tag;
    }
}
