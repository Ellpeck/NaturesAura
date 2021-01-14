package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorTreeRitual implements IComponentProcessor {

    private TreeRitualRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("tree_ritual", provider.get("recipe").asString());
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return null;
        if (key.startsWith("input")) {
            int id = Integer.parseInt(key.substring(5)) - 1;
            if (this.recipe.ingredients.length > id)
                return PatchouliCompat.ingredientVariable(this.recipe.ingredients[id]);
            else
                return null;
        } else {
            switch (key) {
                case "output":
                    return IVariable.from(this.recipe.result);
                case "sapling":
                    return PatchouliCompat.ingredientVariable(this.recipe.saplingType);
                case "name":
                    return IVariable.wrap(this.recipe.result.getDisplayName().getString());
                default:
                    return null;
            }
        }
    }
}
