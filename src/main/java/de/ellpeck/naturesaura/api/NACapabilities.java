package de.ellpeck.naturesaura.api;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public final class NACapabilities {

    @CapabilityInject(IAuraContainer.class)
    public static Capability<IAuraContainer> auraContainer;

    @CapabilityInject(IAuraRecharge.class)
    public static Capability<IAuraRecharge> auraRecharge;

    @CapabilityInject(IAuraChunk.class)
    public static Capability<IAuraChunk> auraChunk;

    public static class StorageImpl<T> implements IStorage<T> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {

        }
    }
}
