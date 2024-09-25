package de.ellpeck.naturesaura.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

import java.util.List;

public class AnimalSpawnerRecipe extends ModRecipe {

    public final List<Ingredient> ingredients;
    public final EntityType<?> entity;
    public final int aura;
    public final int time;

    public AnimalSpawnerRecipe(ResourceLocation entityType, int aura, int time, List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.entity = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        this.aura = aura;
        this.time = time;
    }

    public Entity makeEntity(Level level, BlockPos pos) {
        // passed position is zero on the client, so we don't want to do initialization stuff for the entity
        if (pos == BlockPos.ZERO)
            return this.entity.create(level);
        return this.entity.create((ServerLevel) level, null, pos, MobSpawnType.SPAWNER, false, false);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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

    public static class Serializer implements RecipeSerializer<AnimalSpawnerRecipe> {

        private static final MapCodec<AnimalSpawnerRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("entity").forGetter(r -> BuiltInRegistries.ENTITY_TYPE.getKey(r.entity)),
            Codec.INT.fieldOf("aura").forGetter(r -> r.aura),
            Codec.INT.fieldOf("time").forGetter(r -> r.time),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients)
        ).apply(i, AnimalSpawnerRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, AnimalSpawnerRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(AnimalSpawnerRecipe.Serializer.CODEC.codec());

        @Override
        public MapCodec<AnimalSpawnerRecipe> codec() {
            return Serializer.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnimalSpawnerRecipe> streamCodec() {
            return Serializer.STREAM_CODEC;
        }

    }

}
