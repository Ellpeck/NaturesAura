package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityRFConverter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockRFConverter extends BlockContainerImpl {

    public BlockRFConverter() {
        super(Material.ROCK, "rf_converter", TileEntityRFConverter.class, "rf_converter");
        this.setSoundType(SoundType.STONE);
        this.setHardness(3F);
    }
}
