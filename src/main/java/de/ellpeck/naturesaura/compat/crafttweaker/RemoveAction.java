package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IAction;
import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RemoveAction implements IRuntimeAction {
    private final Map<ResourceLocation, ?> registry;
    private final List<ResourceLocation> recipes;

    public RemoveAction(Map<ResourceLocation, ?> registry, List<ResourceLocation> recipes) {
        this.registry = registry;
        this.recipes = recipes;
    }

    @Override
    public void apply() {
        for (ResourceLocation recipe : this.recipes)
            this.registry.remove(recipe);
    }

    @Override
    public String describe() {
        return "Removing recipes " + this.recipes;
    }
}
