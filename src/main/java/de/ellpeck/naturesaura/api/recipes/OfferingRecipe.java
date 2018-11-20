package de.ellpeck.naturesaura.api.recipes;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class OfferingRecipe {

    public final ResourceLocation name;
    public final AmountIngredient input;
    public final Ingredient startItem;
    public final ItemStack output;

    public OfferingRecipe(ResourceLocation name, AmountIngredient input, Ingredient startItem, ItemStack output) {
        this.name = name;
        this.input = input;
        this.startItem = startItem;
        this.output = output;
    }

    public OfferingRecipe register() {
        NaturesAuraAPI.OFFERING_RECIPES.put(this.name, this);
        return this;
    }
}
