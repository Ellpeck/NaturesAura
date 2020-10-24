package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityWeatherChanger;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;

import java.util.function.Supplier;

public class BlockWeatherChanger extends BlockContainerImpl {
    public BlockWeatherChanger() {
        super("weather_changer", TileEntityWeatherChanger::new, Properties.from(Blocks.STONE_BRICKS));
    }
}
