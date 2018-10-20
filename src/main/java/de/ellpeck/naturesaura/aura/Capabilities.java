package de.ellpeck.naturesaura.aura;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public final class Capabilities {

    @CapabilityInject(IAuraContainer.class)
    public static Capability<IAuraContainer> auraContainer;

    @CapabilityInject(IAuraRecharge.class)
    public static Capability<IAuraRecharge> auraRecharge;

    public static class CapabilityAuraContainer implements IStorage<IAuraContainer> {

        @Override
        public NBTBase writeNBT(Capability<IAuraContainer> capability, IAuraContainer instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IAuraContainer> capability, IAuraContainer instance, EnumFacing side, NBTBase nbt) {

        }
    }

    public static class CapabilityAuraRecharge implements IStorage<IAuraRecharge>{

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IAuraRecharge> capability, IAuraRecharge instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IAuraRecharge> capability, IAuraRecharge instance, EnumFacing side, NBTBase nbt) {

        }
    }
}
