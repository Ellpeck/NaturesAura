package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityAuraBloom extends BlockEntityImpl implements ITickableBlockEntity {

    public boolean justGenerated;

    public BlockEntityAuraBloom(BlockPos pos, BlockState state) {
        this(ModBlockEntities.AURA_BLOOM, pos, state);
    }

    protected BlockEntityAuraBloom(BlockEntityType<BlockEntityAuraBloom> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // Doing this in validate() creates a loading deadlock for some reason...
    @Override
    public void tick() {
        if (this.level.isClientSide || !this.justGenerated)
            return;
        this.generateAura(150000);
        this.justGenerated = false;
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.writeNBT(compound, type, registries);
        if (type == SaveType.TILE)
            compound.putBoolean("just_generated", this.justGenerated);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type, HolderLookup.Provider registries) {
        super.readNBT(compound, type, registries);
        if (type == SaveType.TILE)
            this.justGenerated = compound.getBoolean("just_generated");
    }
}
