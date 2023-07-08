package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreeRitualRecipe extends ModRecipe {

    public final Ingredient saplingType;
    public final Ingredient[] ingredients;
    public final ItemStack result;
    public final int time;

    public TreeRitualRecipe(ResourceLocation name, Ingredient saplingType, ItemStack result, int time, Ingredient... ingredients) {
        super(name);
        this.saplingType = saplingType;
        this.ingredients = ingredients;
        this.result = result;
        this.time = time;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return this.result;
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

        @Override
        public TreeRitualRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            List<Ingredient> ings = new ArrayList<>();
            for (var element : json.getAsJsonArray("ingredients"))
                ings.add(Ingredient.fromJson(element));
            return new TreeRitualRecipe(
                    recipeId,
                    Ingredient.fromJson(json.getAsJsonObject("sapling")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true),
                    json.get("time").getAsInt(),
                    ings.toArray(new Ingredient[0]));
        }

        @Nullable
        @Override
        public TreeRitualRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            var ings = new Ingredient[buffer.readInt()];
            for (var i = 0; i < ings.length; i++)
                ings[i] = Ingredient.fromNetwork(buffer);
            return new TreeRitualRecipe(
                    recipeId,
                    Ingredient.fromNetwork(buffer),
                    buffer.readItem(),
                    buffer.readInt(),
                    ings);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TreeRitualRecipe recipe) {
            buffer.writeInt(recipe.ingredients.length);
            for (var ing : recipe.ingredients)
                ing.toNetwork(buffer);
            recipe.saplingType.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.time);
        }
    }
}
