package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;

public class TileEntityProjectileGenerator extends TileEntityImpl {

    public Direction nextSide = Direction.NORTH;

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.setInteger("next_side", this.nextSide.getHorizontalIndex());
        }
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.nextSide = Direction.byHorizontalIndex(compound.getInteger("next_side"));
        }
    }
}
