package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityProjectileGenerator extends BlockEntityImpl {

    public Direction nextSide = Direction.NORTH;

    public BlockEntityProjectileGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROJECTILE_GENERATOR, pos, state);
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type != SaveType.BLOCK)
            compound.putInt("next_side", this.nextSide.ordinal());
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type != SaveType.BLOCK)
            this.nextSide = Direction.values()[compound.getInt("next_side")];
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
