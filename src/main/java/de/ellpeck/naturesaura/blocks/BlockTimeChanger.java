package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityTimeChanger;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTimeChanger extends BlockContainerImpl {
    public BlockTimeChanger() {
        super("time_changer", TileEntityTimeChanger.class, "time_changer", ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));
    }
}
