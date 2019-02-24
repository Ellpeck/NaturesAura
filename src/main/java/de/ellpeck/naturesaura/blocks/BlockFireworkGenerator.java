package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityFireworkGenerator;
import net.minecraft.block.material.Material;

public class BlockFireworkGenerator extends BlockContainerImpl {
    public BlockFireworkGenerator() {
        super(Material.ROCK, "firework_generator", TileEntityFireworkGenerator.class, "firework_generator");
    }
}
