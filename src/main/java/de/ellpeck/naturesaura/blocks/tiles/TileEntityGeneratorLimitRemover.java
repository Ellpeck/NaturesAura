package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityGeneratorLimitRemover extends TileEntityImpl {

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos, this.pos.add(1, 2, 1));
    }
}
