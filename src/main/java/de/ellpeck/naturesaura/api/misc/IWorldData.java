package de.ellpeck.naturesaura.api.misc;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IWorldData extends ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    static IWorldData getWorldData(World world) {
        if (world.hasCapability(NaturesAuraAPI.capWorldData, null))
            return world.getCapability(NaturesAuraAPI.capWorldData, null);
        return null;
    }

    static IWorldData getOverworldData(World world) {
        if (!world.isRemote) {
            return getWorldData(world.getMinecraftServer().getWorld(DimensionType.OVERWORLD.getId()));
        }
        return getWorldData(world);
    }

    IItemHandlerModifiable getEnderStorage(String name);

    boolean isEnderStorageLocked(String name);
}
