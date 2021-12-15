package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnimalSpawnerRecipe extends ModRecipe {

    public final Ingredient[] ingredients;
    public final EntityType<?> entity;
    public final int aura;
    public final int time;

    public AnimalSpawnerRecipe(ResourceLocation name, EntityType<?> entity, int aura, int time, Ingredient... ingredients) {
        super(name);
        this.ingredients = ingredients;
        this.entity = entity;
        this.aura = aura;
        this.time = time;
    }

    public Entity makeEntity(Level level, BlockPos pos) {
        // passed position is zero on the client, so we don't want to do initialization stuff for the entity
        if (pos == BlockPos.ZERO)
            return this.entity.create(level);
        return this.entity.create((ServerLevel) level, null, null, null, pos, MobSpawnType.SPAWNER, false, false);
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ANIMAL_SPAWNER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ANIMAL_SPAWNER_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AnimalSpawnerRecipe> {

        @Override
        public AnimalSpawnerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (var e : json.getAsJsonArray("ingredients"))
                ingredients.add(Ingredient.fromJson(e));
            return new AnimalSpawnerRecipe(recipeId,
                    ForgeRegistries.ENTITIES.getValue(new ResourceLocation(json.get("entity").getAsString())),
                    json.get("aura").getAsInt(),
                    json.get("time").getAsInt(),
                    ingredients.toArray(new Ingredient[0]));
        }

        @Nullable
        @Override
        public AnimalSpawnerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            var ings = new Ingredient[buffer.readInt()];
            for (var i = 0; i < ings.length; i++)
                ings[i] = Ingredient.fromNetwork(buffer);
            return new AnimalSpawnerRecipe(
                    recipeId,
                    ForgeRegistries.ENTITIES.getValue(buffer.readResourceLocation()),
                    buffer.readInt(),
                    buffer.readInt(),
                    ings);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AnimalSpawnerRecipe recipe) {
            buffer.writeInt(recipe.ingredients.length);
            for (var ing : recipe.ingredients)
                ing.toNetwork(buffer);
            buffer.writeResourceLocation(recipe.entity.getRegistryName());
            buffer.writeInt(recipe.aura);
            buffer.writeInt(recipe.time);
        }
    }
}
