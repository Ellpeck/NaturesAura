package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class AuraChunkProvider implements INBTSerializable<CompoundTag> {

    private IAuraChunk auraChunk;

    public IAuraChunk get(LevelChunk chunk) {
        if (this.auraChunk == null)
            this.auraChunk = NaturesAuraAPI.instance().createAuraChunk();
        this.auraChunk.ensureInitialized(chunk);
        return this.auraChunk;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.get(null).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.get(null).deserializeNBT(nbt);
    }

}
