package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityChorusGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.world.level.block.Blocks;

public class BlockChorusGenerator extends BlockContainerImpl implements ICustomBlockState, IPickaxeBreakable {

    public BlockChorusGenerator() {
        super("chorus_generator", BlockEntityChorusGenerator.class, Properties.ofFullCopy(Blocks.END_STONE));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
