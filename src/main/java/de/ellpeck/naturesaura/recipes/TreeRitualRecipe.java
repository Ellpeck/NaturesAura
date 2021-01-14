package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

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
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.TREE_RITUAL_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.TREE_RITUAL_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TreeRitualRecipe> {
        @Override
        public TreeRitualRecipe read(ResourceLocation recipeId, JsonObject json) {
            List<Ingredient> ings = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray("ingredients"))
                ings.add(Ingredient.deserialize(element));
            return new TreeRitualRecipe(
                    recipeId,
                    Ingredient.deserialize(json.getAsJsonObject("sapling")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true),
                    json.get("time").getAsInt(),
                    ings.toArray(new Ingredient[0]));
        }

        @Nullable
        @Override
        public TreeRitualRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient[] ings = new Ingredient[buffer.readInt()];
            for (int i = 0; i < ings.length; i++)
                ings[i] = Ingredient.read(buffer);
            return new TreeRitualRecipe(
                    recipeId,
                    Ingredient.read(buffer),
                    buffer.readItemStack(),
                    buffer.readInt(),
                    ings);
        }

        @Override
        public void write(PacketBuffer buffer, TreeRitualRecipe recipe) {
            buffer.writeInt(recipe.ingredients.length);
            for (Ingredient ing : recipe.ingredients)
                ing.write(buffer);
            recipe.saplingType.write(buffer);
            buffer.writeItemStack(recipe.result);
            buffer.writeInt(recipe.time);
        }
    }
}
