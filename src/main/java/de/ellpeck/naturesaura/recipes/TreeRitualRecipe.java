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
import java.util.ArrayList;
import java.util.List;

public class TreeRitualRecipe extends ModRecipe {

    public final Ingredient saplingType;
    public final List<Ingredient> ingredients;
    public final ItemStack output;
    public final int time;

    public TreeRitualRecipe(Ingredient saplingType, ItemStack output, int time, List<Ingredient> ingredients) {
        this.saplingType = saplingType;
        this.ingredients = ingredients;
        this.output = output;
        this.time = time;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TREE_RITUAL_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TREE_RITUAL_TYPE;
    }

    public static class Serializer implements RecipeSerializer<TreeRitualRecipe> {

        private static final Codec<TreeRitualRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
                Ingredient.CODEC.fieldOf("sapling").forGetter(r -> r.saplingType),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("time").forGetter(r -> r.time),
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients)
        ).apply(i, TreeRitualRecipe::new));

        @Override
        public Codec<TreeRitualRecipe> codec() {
            return Serializer.CODEC;
        }

        @Nullable
        @Override
        public TreeRitualRecipe fromNetwork(FriendlyByteBuf buffer) {
            var ingredients = new ArrayList<Ingredient>();
            for (var i = buffer.readInt(); i > 0; i--)
                ingredients.add(Ingredient.fromNetwork(buffer));
            return new TreeRitualRecipe(Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readInt(), ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TreeRitualRecipe recipe) {
            buffer.writeInt(recipe.ingredients.size());
            for (var ing : recipe.ingredients)
                ing.toNetwork(buffer);
            recipe.saplingType.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.time);
        }

    }

}
