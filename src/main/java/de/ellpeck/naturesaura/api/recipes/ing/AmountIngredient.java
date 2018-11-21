package de.ellpeck.naturesaura.api.recipes.ing;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class AmountIngredient extends Ingredient {

    public final Ingredient delegate;
    public final int amount;
    private ItemStack[] matchingStacks;

    public AmountIngredient(Ingredient delegate, int amount) {
        super(0);
        this.delegate = delegate;
        this.amount = amount;
    }

    public AmountIngredient(ItemStack stack) {
        this(Ingredient.fromStacks(stack), stack.getCount());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if (this.matchingStacks == null) {
            ItemStack[] delegate = this.delegate.getMatchingStacks();
            this.matchingStacks = new ItemStack[delegate.length];
            for (int i = 0; i < delegate.length; i++) {
                ItemStack copy = delegate[i].copy();
                copy.setCount(this.amount);
                this.matchingStacks[i] = copy;
            }
        }
        return this.matchingStacks;
    }

    @Override
    public boolean apply(ItemStack stack) {
        if (!this.delegate.apply(stack))
            return false;
        return stack.getCount() >= this.amount;
    }

    @Override
    public IntList getValidItemStacksPacked() {
        return this.delegate.getValidItemStacksPacked();
    }

    @Override
    public boolean isSimple() {
        return this.delegate.isSimple();
    }
}
