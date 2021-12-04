package de.ellpeck.naturesaura.api.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;

public class WeightedOre extends WeightedEntry.IntrusiveBase {

    public final ResourceLocation tag;

    public WeightedOre(ResourceLocation tag, int weight) {
        super(weight);
        this.tag = tag;
    }
}
