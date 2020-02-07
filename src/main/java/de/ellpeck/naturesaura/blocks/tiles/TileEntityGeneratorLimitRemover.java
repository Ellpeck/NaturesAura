package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityGeneratorLimitRemover extends TileEntityImpl {

    public TileEntityGeneratorLimitRemover() {
        super(ModTileEntities.GENERATOR_LIMIT_REMOVER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos, this.pos.add(1, 2, 1));
    }
}
