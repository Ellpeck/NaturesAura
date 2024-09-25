package de.ellpeck.naturesaura.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class AltarRecipe extends ModRecipe {

    public final Ingredient input;
    public final ItemStack output;
    public final Ingredient catalyst;
    public final int aura;
    public final int time;

    public AltarRecipe(Ingredient input, ItemStack output, Ingredient catalyst, int aura, int time) {
        this.input = input;
        this.output = output;
        this.catalyst = catalyst;
        this.aura = aura;
        this.time = time;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALTAR_TYPE;
    }

    public static class Serializer implements RecipeSerializer<AltarRecipe> {

        private static final MapCodec<AltarRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
            ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
            Ingredient.CODEC.optionalFieldOf("catalyst", Ingredient.EMPTY).forGetter(r -> r.catalyst),
            Codec.INT.fieldOf("aura").forGetter(r -> r.aura),
            Codec.INT.fieldOf("time").forGetter(r -> r.time)
        ).apply(i, AltarRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(Serializer.CODEC.codec());

        @Override
        public MapCodec<AltarRecipe> codec() {
            return Serializer.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodec() {
            return Serializer.STREAM_CODEC;
        }

    }

}
