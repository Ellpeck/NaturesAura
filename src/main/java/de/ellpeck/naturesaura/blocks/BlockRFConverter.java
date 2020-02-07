package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityRFConverter;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockRFConverter extends BlockContainerImpl {

    public BlockRFConverter() {
        super("rf_converter", TileEntityRFConverter::new, Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3));
    }
}
