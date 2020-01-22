package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class TileEntityProjectileGenerator extends TileEntityImpl {

    public Direction nextSide = Direction.NORTH;

    public TileEntityProjectileGenerator() {
        super(ModTileEntities.PROJECTILE_GENERATOR);
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.putInt("next_side", this.nextSide.getHorizontalIndex());
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.nextSide = Direction.byHorizontalIndex(compound.getInt("next_side"));
        }
    }
}
