package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityHopperUpgrade;
import net.minecraft.block.material.Material;

public class BlockHopperUpgrade extends BlockContainerImpl {
    public BlockHopperUpgrade() {
        super(Material.IRON, "hopper_upgrade", TileEntityHopperUpgrade.class, "hopper_upgrade");
    }
}
