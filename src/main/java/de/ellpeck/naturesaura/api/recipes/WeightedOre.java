package de.ellpeck.naturesaura.api.recipes;

import net.minecraft.util.WeightedRandom;

public class WeightedOre extends WeightedRandom.Item {

    public final String name;

    public WeightedOre(String name, int weight) {
        super(weight);
        this.name = name;
    }
}
