package de.ellpeck.naturesaura.api.misc;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface ILevelData {

    static ILevelData getLevelData(Level level) {
        return NaturesAuraAPI.instance().getLevelData(level);
    }

    static ILevelData getOverworldData(Level level) {
        if (!level.isClientSide)
            return ILevelData.getLevelData(level.getServer().getLevel(Level.OVERWORLD));
        return ILevelData.getLevelData(level);
    }

    IItemHandlerModifiable getEnderStorage(String name);

    boolean isEnderStorageLocked(String name);

}
