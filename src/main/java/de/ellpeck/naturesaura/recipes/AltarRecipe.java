package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class AltarRecipe extends ModRecipe {

    public final Ingredient input;
    public final ItemStack output;
    public final IAuraType requiredType;
    public final Ingredient catalyst;
    public final int aura;
    public final int time;

    public AltarRecipe(ResourceLocation name, Ingredient input, ItemStack output, IAuraType requiredType, Ingredient catalyst, int aura, int time) {
        super(name);
        this.input = input;
        this.output = output;
        this.requiredType = requiredType;
        this.catalyst = catalyst;
        this.aura = aura;
        this.time = time;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_SERIAIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.ALTAR_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AltarRecipe> {

        @Override
        public AltarRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new AltarRecipe(
                    recipeId,
                    Ingredient.deserialize(json.getAsJsonObject("input")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true),
                    NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(json.get("aura_type").getAsString())),
                    json.has("catalyst") ? Ingredient.deserialize(json.getAsJsonObject("catalyst")) : Ingredient.EMPTY,
                    json.get("aura").getAsInt(),
                    json.get("time").getAsInt());
        }

        @Nullable
        @Override
        public AltarRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new AltarRecipe(
                    recipeId,
                    Ingredient.read(buffer),
                    buffer.readItemStack(),
                    NaturesAuraAPI.AURA_TYPES.get(buffer.readResourceLocation()),
                    Ingredient.read(buffer),
                    buffer.readInt(),
                    buffer.readInt());
        }

        @Override
        public void write(PacketBuffer buffer, AltarRecipe recipe) {
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeResourceLocation(recipe.requiredType.getName());
            recipe.catalyst.write(buffer);
            buffer.writeInt(recipe.aura);
            buffer.writeInt(recipe.time);
        }
    }
}
