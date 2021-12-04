package de.ellpeck.naturesaura.api.aura.type;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IAuraType {

    static IAuraType forLevel(Level level) {
        IAuraType highestType = NaturesAuraAPI.TYPE_OTHER;
        for (IAuraType type : NaturesAuraAPI.AURA_TYPES.values())
            if (type.isPresentInLevel(level) && type.getPriority() > highestType.getPriority())
                highestType = type;
        return highestType;
    }

    ResourceLocation getName();

    boolean isPresentInLevel(Level level);

    int getColor();

    int getPriority();

    default boolean isSimilar(IAuraType type) {
        return this == type;
    }
}
