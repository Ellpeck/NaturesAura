package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityAuraBloom extends TileEntityImpl implements ITickableTileEntity {

    public boolean justGenerated;

    public TileEntityAuraBloom() {
        this(ModTileEntities.AURA_BLOOM);
    }

    protected TileEntityAuraBloom(TileEntityType<TileEntityAuraBloom> type) {
        super(type);
    }

    // Doing this in validate() creates a loading deadlock for some reason...
    @Override
    public void tick() {
        if (this.world.isRemote || !this.justGenerated)
            return;
        IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, this.pos);
        chunk.storeAura(this.pos, 150000);
        this.justGenerated = false;
    }

    @Override
    public void writeNBT(CompoundNBT compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE)
            compound.putBoolean("just_generated", this.justGenerated);
    }

    @Override
    public void readNBT(CompoundNBT compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE)
            this.justGenerated = compound.getBoolean("just_generated");
    }
}
