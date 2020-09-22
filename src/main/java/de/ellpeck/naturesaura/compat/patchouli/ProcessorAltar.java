package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAltar implements IComponentProcessor {

    private AltarRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("altar", provider.get("recipe").asString());
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return null;
        switch (key) {
            case "input":
                return IVariable.from(this.recipe.input);
            case "output":
                return IVariable.from(this.recipe.output);
            case "catalyst":
                if (this.recipe.catalyst != Ingredient.EMPTY)
                    return IVariable.from(this.recipe.catalyst);
                else
                    return null;
            case "type":
                if (this.recipe.requiredType != null)
                    return IVariable.from(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), this.recipe.requiredType));
                else
                    return null;
            case "name":
                return IVariable.wrap(this.recipe.output.getDisplayName().getString());
            default:
                return null;
        }
    }

    @Override
    public boolean allowRender(String group) {
        return group.isEmpty() || group.equals(this.recipe.catalyst == Ingredient.EMPTY ? "altar" : "catalyst");
    }
}
