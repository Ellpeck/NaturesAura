package de.ellpeck.naturesaura.recipes;

import com.google.gson.JsonObject;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_SERIAIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALTAR_TYPE;
    }

    public ItemStack getDimensionBottle() {
        var bottle = ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), this.requiredType);
        bottle.setHoverName(new TranslatableComponent("info." + NaturesAura.MOD_ID + ".required_aura_type." + this.requiredType.getName()));
        return bottle;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AltarRecipe> {

        @Override
        public AltarRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new AltarRecipe(
                    recipeId,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    CraftingHelper.getItemStack(json.getAsJsonObject("output"), true),
                    NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(json.get("aura_type").getAsString())),
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
                    NaturesAuraAPI.AURA_TYPES.get(buffer.readResourceLocation()),
                    Ingredient.fromNetwork(buffer),
                    buffer.readInt(),
                    buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AltarRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeResourceLocation(recipe.requiredType.getName());
            recipe.catalyst.toNetwork(buffer);
            buffer.writeInt(recipe.aura);
            buffer.writeInt(recipe.time);
        }
    }
}
