package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTimeChanger extends BlockContainerImpl {
    public BlockTimeChanger() {
        super("time_changer", ModTileEntities.TIME_CHANGER, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));
    }
}
