package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityMossGenerator;
import net.minecraft.block.material.Material;

public class BlockMossGenerator extends BlockContainerImpl {
    public BlockMossGenerator() {
        super(Material.ROCK, "moss_generator", TileEntityMossGenerator.class, "moss_generator");
    }
}
