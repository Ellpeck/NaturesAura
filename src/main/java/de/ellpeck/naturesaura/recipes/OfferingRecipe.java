package de.ellpeck.naturesaura.recipes;

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
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.OFFERING_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.OFFERING_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<OfferingRecipe> {
        @Override
        public OfferingRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new OfferingRecipe(
                    recipeId,
                    Ingredient.deserialize(json.get("input")),
                    Ingredient.deserialize(json.get("start_item")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true));
        }

        @Nullable
        @Override
        public OfferingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new OfferingRecipe(
                    recipeId,
                    Ingredient.read(buffer),
                    Ingredient.read(buffer),
                    buffer.readItemStack());
        }

        @Override
        public void write(PacketBuffer buffer, OfferingRecipe recipe) {
            recipe.input.write(buffer);
            recipe.startItem.write(buffer);
            buffer.writeItemStack(recipe.output);
        }
    }
}
