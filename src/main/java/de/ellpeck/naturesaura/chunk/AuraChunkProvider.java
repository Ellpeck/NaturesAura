package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuraChunkProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    private final Chunk chunk;
    private IAuraChunk auraChunk;

    public AuraChunkProvider(Chunk chunk) {
        this.chunk = chunk;
    }

    private IAuraChunk getAuraChunk() {
        if (this.auraChunk == null)
            this.auraChunk = new AuraChunk(this.chunk, IAuraType.forWorld(this.chunk.getWorld()));
        return this.auraChunk;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.capAuraChunk ? LazyOptional.of(() -> (T) this.getAuraChunk()) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.getAuraChunk().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.getAuraChunk().deserializeNBT(nbt);
    }
}
