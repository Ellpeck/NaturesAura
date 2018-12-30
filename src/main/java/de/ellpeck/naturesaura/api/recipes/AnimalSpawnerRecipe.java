package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.Function;

public class AnimalSpawnerRecipe {

    public final ResourceLocation name;
    public final Ingredient[] ingredients;
    public final Function<World, Entity> entity;
    public final int aura;
    public final int time;

    public AnimalSpawnerRecipe(ResourceLocation name, Function<World, Entity> entity, int aura, int time, Ingredient... ingredients) {
        this.name = name;
        this.ingredients = ingredients;
        this.entity = entity;
        this.aura = aura;
        this.time = time;
    }

    public AnimalSpawnerRecipe register() {
        NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.put(this.name, this);
        return this;
    }
}
