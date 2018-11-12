package de.ellpeck.naturesaura.api.aura.type;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IAuraType {

    static IAuraType forWorld(World world) {
        for (IAuraType type : NaturesAuraAPI.AURA_TYPES.values())
            if (type.isPresentInWorld(world))
                return type;
        return NaturesAuraAPI.TYPE_OTHER;
    }

    ResourceLocation getName();

    boolean isPresentInWorld(World world);

    int getColor();
}
