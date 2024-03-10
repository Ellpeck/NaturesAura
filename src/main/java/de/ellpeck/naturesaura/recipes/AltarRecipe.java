package de.ellpeck.naturesaura.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

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
    public ItemStack getResultItem(RegistryAccess access) {
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

        private static final Codec<AltarRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                Ingredient.CODEC.fieldOf("catalyst").forGetter(r -> r.catalyst),
                Codec.INT.fieldOf("aura").forGetter(r -> r.aura),
                Codec.INT.fieldOf("time").forGetter(r -> r.time)
        ).apply(i, AltarRecipe::new));

        @Override
        public Codec<AltarRecipe> codec() {
            return Serializer.CODEC;
        }

        @Nullable
        @Override
        public AltarRecipe fromNetwork(FriendlyByteBuf buffer) {
            return new AltarRecipe(Ingredient.fromNetwork(buffer), buffer.readItem(), Ingredient.fromNetwork(buffer), buffer.readInt(), buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AltarRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            recipe.catalyst.toNetwork(buffer);
            buffer.writeInt(recipe.aura);
            buffer.writeInt(recipe.time);
        }

    }

}
