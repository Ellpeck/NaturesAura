/*
package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AltarRecipe;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

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
                return PatchouliCompat.ingredientVariable(this.recipe.input);
            case "output":
                return IVariable.from(this.recipe.output);
            case "catalyst":
                if (this.recipe.catalyst != Ingredient.EMPTY)
                    return PatchouliCompat.ingredientVariable(this.recipe.catalyst);
                else
                    return null;
            case "type":
                if (this.recipe.requiredType != null)
                    return IVariable.from(this.recipe.getDimensionBottle());
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
*/
