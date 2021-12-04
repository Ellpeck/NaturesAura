package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntityType;

public class BlockEntityAuraBloom extends BlockEntityImpl implements ITickableBlockEntity {

    public boolean justGenerated;

    public BlockEntityAuraBloom() {
        this(ModTileEntities.AURA_BLOOM);
    }

    protected BlockEntityAuraBloom(BlockEntityType<BlockEntityAuraBloom> type) {
        super(type);
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
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putBoolean("just_generated", this.justGenerated);
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE)
            this.justGenerated = compound.getBoolean("just_generated");
    }
}
