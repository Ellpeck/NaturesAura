package de.ellpeck.naturesaura.api.misc;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IWorldData extends ICapabilityProvider, INBTSerializable<CompoundNBT> {

    static IWorldData getWorldData(World world) {
        return world.getCapability(NaturesAuraAPI.capWorldData, null).orElse(null);
    }

    static IWorldData getOverworldData(World world) {
        if (!world.isRemote)
            return getWorldData(world.getServer().getWorld(World.field_234918_g_));
        return getWorldData(world);
    }

    IItemHandlerModifiable getEnderStorage(String name);

    boolean isEnderStorageLocked(String name);
}
