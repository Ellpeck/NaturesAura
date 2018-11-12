package de.ellpeck.naturesaura.api.aura.type;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class BasicAuraType implements IAuraType {

    private final ResourceLocation name;
    private final DimensionType dimension;
    private final int color;

    public BasicAuraType(ResourceLocation name, DimensionType dimension, int color) {
        this.name = name;
        this.dimension = dimension;
        this.color = color;
    }

    public BasicAuraType register() {
        NaturesAuraAPI.AURA_TYPES.put(this.name, this);
        return this;
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public boolean isPresentInWorld(World world) {
        return world.provider.getDimensionType() == this.dimension;
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
