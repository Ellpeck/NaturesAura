package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IAction;
import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AddAction<T> implements IRuntimeAction {

    private final Map<ResourceLocation, T> registry;
    private final ResourceLocation res;
    private final T recipe;

    public AddAction(Map<ResourceLocation, T> registry, ResourceLocation res, T recipe) {
        this.registry = registry;
        this.res = res;
        this.recipe = recipe;
    }

    @Override
    public void apply() {
        this.registry.put(this.res, this.recipe);
    }

    @Override
    public String describe() {
        return "Adding recipe " + this.res;
    }
}
