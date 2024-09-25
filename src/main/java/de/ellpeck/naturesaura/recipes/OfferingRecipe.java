package de.ellpeck.naturesaura.recipes;

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
    public ItemStack getResultItem(HolderLookup.Provider registries) {
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

        private static final MapCodec<OfferingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
            Ingredient.CODEC.fieldOf("start_item").forGetter(r -> r.startItem),
            ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(i, OfferingRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, OfferingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(OfferingRecipe.Serializer.CODEC.codec());

        @Override
        public MapCodec<OfferingRecipe> codec() {
            return Serializer.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OfferingRecipe> streamCodec() {
            return Serializer.STREAM_CODEC;
        }

    }

}
