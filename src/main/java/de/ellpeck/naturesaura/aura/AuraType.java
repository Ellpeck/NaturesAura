package de.ellpeck.naturesaura.aura;

import net.minecraft.world.World;

public enum AuraType {
    OVERWORLD,
    NETHER,
    END,
    OTHER;

    public boolean isPresent(World world) {
        return forWorld(world) == this;
    }

    public static AuraType forWorld(World world) {
        switch (world.provider.getDimensionType()) {
            case OVERWORLD:
                return OVERWORLD;
            case NETHER:
                return NETHER;
            case THE_END:
                return END;
            default:
                return OTHER;
        }
    }
}
