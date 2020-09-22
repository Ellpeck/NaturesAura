package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
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

    public Entity makeEntity(World world, double x, double y, double z) {
        Entity entity = this.entity.create(world);
        if (x == 0 && y == 0 && z == 0)
            return entity;
        entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360F), 0F);
        if (entity instanceof MobEntity) {
            MobEntity living = (MobEntity) entity;
            living.rotationYawHead = entity.rotationYaw;
            living.renderYawOffset = entity.rotationYaw;
            living.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(living.getPosition()), SpawnReason.SPAWNER, null, null);
        }
        return entity;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ANIMAL_SPAWNER_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.ANIMAL_SPAWNER_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AnimalSpawnerRecipe> {
        @Override
        public AnimalSpawnerRecipe read(ResourceLocation recipeId, JsonObject json) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray("ingredients"))
                ingredients.add(Ingredient.deserialize(e));
            return new AnimalSpawnerRecipe(recipeId,
                    ForgeRegistries.ENTITIES.getValue(new ResourceLocation(json.get("entity").getAsString())),
                    json.get("aura").getAsInt(),
                    json.get("time").getAsInt(),
                    ingredients.toArray(new Ingredient[0]));
        }

        @Nullable
        @Override
        public AnimalSpawnerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient[] ings = new Ingredient[buffer.readInt()];
            for (int i = 0; i < ings.length; i++)
                ings[i] = Ingredient.read(buffer);
            return new AnimalSpawnerRecipe(
                    recipeId,
                    ForgeRegistries.ENTITIES.getValue(buffer.readResourceLocation()),
                    buffer.readInt(),
                    buffer.readInt(),
                    ings);
        }

        @Override
        public void write(PacketBuffer buffer, AnimalSpawnerRecipe recipe) {
            buffer.writeInt(recipe.ingredients.length);
            for (Ingredient ing : recipe.ingredients)
                ing.write(buffer);
            buffer.writeResourceLocation(recipe.entity.getRegistryName());
            buffer.writeInt(recipe.aura);
            buffer.writeInt(recipe.time);
        }
    }
}
