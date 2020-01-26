package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityBlastFurnaceBooster;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class BlockBlastFurnaceBooster extends BlockContainerImpl {
    public BlockBlastFurnaceBooster() {
        super("blast_furnace_booster", TileEntityBlastFurnaceBooster::new, Block.Properties.from(Blocks.BLAST_FURNACE));
    }
}
