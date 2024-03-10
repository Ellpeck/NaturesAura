package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuraChunkProvider implements INBTSerializable<CompoundTag> {

    private final LevelChunk chunk;
    private IAuraChunk auraChunk;

    public AuraChunkProvider(LevelChunk chunk) {
        this.chunk = chunk;
    }

    private IAuraChunk getAuraChunk() {
        if (this.auraChunk == null)
            this.auraChunk = new AuraChunk(this.chunk, IAuraType.forLevel(this.chunk.getLevel()));
        return this.auraChunk;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.CAP_AURA_CHUNK ? this.lazyChunk.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.getAuraChunk().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getAuraChunk().deserializeNBT(nbt);
    }
}
