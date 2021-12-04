package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class OfferingRecipe extends ModRecipe {

    public final Ingredient input;
    public final Ingredient startItem;
    public final ItemStack output;

    public OfferingRecipe(ResourceLocation name, Ingredient input, Ingredient startItem, ItemStack output) {
        super(name);
        this.input = input;
        this.startItem = startItem;
        this.output = output;
    }

    @Override
    public ItemStack getResultItem() {
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

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<OfferingRecipe> {

        @Override
        public OfferingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new OfferingRecipe(
                    recipeId,
                    Ingredient.fromJson(json.get("input")),
                    Ingredient.fromJson(json.get("start_item")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true));
        }

        @Nullable
        @Override
        public OfferingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new OfferingRecipe(
                    recipeId,
                    Ingredient.fromNetwork(buffer),
                    Ingredient.fromNetwork(buffer),
                    buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OfferingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            recipe.startItem.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }
    }
}
