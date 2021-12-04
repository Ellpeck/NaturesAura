package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEntityGeneratorLimitRemover extends BlockEntityImpl {

    public BlockEntityGeneratorLimitRemover() {
        super(ModTileEntities.GENERATOR_LIMIT_REMOVER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.worldPosition, this.worldPosition.add(1, 2, 1));
    }
}
