package de.ellpeck.naturesaura.api.recipes;

import net.minecraft.block.Block;
import net.minecraft.tags.Tag;
import net.minecraft.util.WeightedRandom;

public class WeightedOre extends WeightedRandom.Item {

    public final Tag<Block> tag;

    public WeightedOre(Tag<Block> tag, int weight) {
        super(weight);
        this.tag = tag;
    }
}
