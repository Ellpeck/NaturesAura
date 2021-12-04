package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Direction;

public class BlockEntityProjectileGenerator extends BlockEntityImpl {

    public Direction nextSide = Direction.NORTH;

    public BlockEntityProjectileGenerator() {
        super(ModTileEntities.PROJECTILE_GENERATOR);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK) {
            compound.putInt("next_side", this.nextSide.getHorizontalIndex());
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK) {
            this.nextSide = Direction.byHorizontalIndex(compound.getInt("next_side"));
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
