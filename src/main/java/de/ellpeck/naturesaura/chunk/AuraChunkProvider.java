package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuraChunkProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final LevelChunk chunk;
    private final LazyOptional<IAuraChunk> lazyChunk = LazyOptional.of(this::getAuraChunk);
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
        return capability == NaturesAuraAPI.capAuraChunk ? this.lazyChunk.cast() : LazyOptional.empty();
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
