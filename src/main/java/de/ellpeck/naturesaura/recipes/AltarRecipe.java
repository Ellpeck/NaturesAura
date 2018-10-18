package de.ellpeck.naturesaura.recipes;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AltarRecipe {

    public static final List<AltarRecipe> RECIPES = new ArrayList<>();

    public final ItemStack input;
    public final ItemStack output;
    public final int aura;
    public final int time;

    public AltarRecipe(ItemStack input, ItemStack output, int aura, int time) {
        this.input = input;
        this.output = output;
        this.aura = aura;
        this.time = time;
    }

    public static AltarRecipe forInput(ItemStack input) {
        for (AltarRecipe recipe : RECIPES) {
            if (recipe.input.isItemEqual(input)) {
                return recipe;
            }
        }
        return null;
    }

    public AltarRecipe add() {
        RECIPES.add(this);
        return this;
    }
}
