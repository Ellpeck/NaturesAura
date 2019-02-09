package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityTimeChanger;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTimeChanger extends BlockContainerImpl {
    public BlockTimeChanger() {
        super(Material.ROCK, "time_changer", TileEntityTimeChanger.class, "time_changer");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2.5F);
    }
}
