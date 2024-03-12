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

public class OfferingRecipe extends ModRecipe {

    public final Ingredient input;
    public final Ingredient startItem;
    public final ItemStack output;

    public OfferingRecipe(Ingredient input, Ingredient startItem, ItemStack output) {
        this.input = input;
        this.startItem = startItem;
        this.output = output;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.OFFERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.OFFERING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<OfferingRecipe> {

        private static final Codec<OfferingRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                Ingredient.CODEC.fieldOf("start_item").forGetter(r -> r.startItem),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(i, OfferingRecipe::new));

        @Override
        public Codec<OfferingRecipe> codec() {
            return Serializer.CODEC;
        }

        @Nullable
        @Override
        public OfferingRecipe fromNetwork(FriendlyByteBuf buffer) {
            return new OfferingRecipe(Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer), buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OfferingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            recipe.startItem.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }

    }

}
