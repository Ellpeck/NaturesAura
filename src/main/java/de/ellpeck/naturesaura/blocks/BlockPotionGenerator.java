package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityPotionGenerator;
import net.minecraft.block.material.Material;

public class BlockPotionGenerator extends BlockContainerImpl {
    public BlockPotionGenerator() {
        super(Material.ROCK, "potion_generator", TileEntityPotionGenerator.class, "potion_generator");
        this.setHardness(5F);
        this.setHarvestLevel("pickaxe", 1);
    }
}
