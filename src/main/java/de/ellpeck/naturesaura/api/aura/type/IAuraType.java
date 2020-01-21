package de.ellpeck.naturesaura.api.aura.type;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface IAuraType {

    static IAuraType forWorld(IWorld world) {
        IAuraType highestType = NaturesAuraAPI.TYPE_OTHER;
        for (IAuraType type : NaturesAuraAPI.AURA_TYPES.values())
            if (type.isPresentInWorld(world) && type.getPriority() > highestType.getPriority())
                highestType = type;
        return highestType;
    }

    ResourceLocation getName();

    boolean isPresentInWorld(IWorld world);

    int getColor();

    int getPriority();

    default boolean isSimilar(IAuraType type) {
        return this == type;
    }
}
