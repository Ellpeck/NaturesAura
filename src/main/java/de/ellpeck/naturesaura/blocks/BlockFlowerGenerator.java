package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityFlowerGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockFlowerGenerator extends BlockContainerImpl {

    public BlockFlowerGenerator() {
        super(Material.WOOD, "flower_generator", TileEntityFlowerGenerator.class, "flower_generator");
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2F);
    }
}
