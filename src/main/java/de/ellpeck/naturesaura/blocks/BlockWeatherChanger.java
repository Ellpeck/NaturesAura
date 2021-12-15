package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWeatherChanger;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.world.level.block.Blocks;

public class BlockWeatherChanger extends BlockContainerImpl implements ICustomBlockState {

    public BlockWeatherChanger() {
        super("weather_changer", BlockEntityWeatherChanger::new, Properties.copy(Blocks.STONE_BRICKS));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
