package de.ellpeck.naturesaura.api.aura.type;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class BasicAuraType implements IAuraType {

    private final ResourceLocation name;
    private final int color;
    private final int priority;
    private final Set<RegistryKey<World>> dimensions = new HashSet<>();

    public BasicAuraType(ResourceLocation name, RegistryKey<World> dimension, int color, int priority) {
        this.name = name;
        this.color = color;
        this.priority = priority;
        if (dimension != null)
            this.dimensions.add(dimension);
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
    public boolean isPresentInWorld(IWorld world) {
        return this.dimensions.isEmpty() || this.dimensions.contains(((World) world).func_234923_W_());
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    public void addDimensionType(RegistryKey<World> type) {
        this.dimensions.add(type);
    }
}
