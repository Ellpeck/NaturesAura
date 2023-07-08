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

public class AltarRecipe extends ModRecipe {

    public final Ingredient input;
    public final ItemStack output;
    public final Ingredient catalyst;
    public final int aura;
    public final int time;

    public AltarRecipe(ResourceLocation name, Ingredient input, ItemStack output, Ingredient catalyst, int aura, int time) {
        super(name);
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

        @Override
        public AltarRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new AltarRecipe(
                    recipeId,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true),
                    json.has("catalyst") ? Ingredient.fromJson(json.getAsJsonObject("catalyst")) : Ingredient.EMPTY,
                    json.get("aura").getAsInt(),
                    json.get("time").getAsInt());
        }

        @Nullable
        @Override
        public AltarRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new AltarRecipe(
                    recipeId,
                    Ingredient.fromNetwork(buffer),
                    buffer.readItem(),
                    Ingredient.fromNetwork(buffer),
                    buffer.readInt(),
                    buffer.readInt());
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
