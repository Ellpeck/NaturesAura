package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorTreeRitual implements IComponentProcessor {

    private TreeRitualRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("tree_ritual", provider.get("recipe").asString());
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return IVariable.empty();
        if (key.startsWith("input")) {
            int id = Integer.parseInt(key.substring(5)) - 1;
            if (this.recipe.ingredients.length > id)
                return IVariable.from(this.recipe.ingredients[id]);
            else
                return IVariable.empty();
        } else {
            switch (key) {
                case "output":
                    return IVariable.from(this.recipe.result);
                case "sapling":
                    return IVariable.from(this.recipe.saplingType);
                case "name":
                    return IVariable.wrap(this.recipe.result.getDisplayName().getString());
                default:
                    return IVariable.empty();
            }
        }
    }
}
