package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class AnimalSpawnerRecipe {

    public final ResourceLocation name;
    public final Ingredient[] ingredients;
    public final ResourceLocation entity;
    public final int aura;
    public final int time;

    public AnimalSpawnerRecipe(ResourceLocation name, ResourceLocation entity, int aura, int time, Ingredient... ingredients) {
        this.name = name;
        this.ingredients = ingredients;
        this.entity = entity;
        this.aura = aura;
        this.time = time;
    }

    public Entity makeEntity(World world) {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(this.entity);
        if (entry == null)
            return null;
        return entry.newInstance(world);
    }

    public AnimalSpawnerRecipe register() {
        NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.put(this.name, this);
        return this;
    }
}
