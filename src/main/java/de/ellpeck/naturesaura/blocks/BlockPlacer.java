package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityPlacer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockPlacer extends BlockContainerImpl {

    public BlockPlacer() {
        super(Material.ROCK, "placer", TileEntityPlacer.class, "placer");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2.5F);
    }
}
