package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityTimeChanger;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTimeChanger extends BlockContainerImpl implements ICustomBlockState {
    public BlockTimeChanger() {
        super("time_changer", TileEntityTimeChanger::new, Properties.create(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
