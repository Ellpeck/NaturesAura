package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityProjectileGenerator extends TileEntityImpl {

    public EnumFacing nextSide = EnumFacing.NORTH;

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.setInteger("next_side", this.nextSide.getHorizontalIndex());
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.nextSide = EnumFacing.byHorizontalIndex(compound.getInteger("next_side"));
        }
    }
}
