package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorTreeRitual implements IComponentProcessor {

    private TreeRitualRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = PatchouliCompat.getRecipe(NaturesAuraAPI.TREE_RITUAL_RECIPES, provider.get("recipe"));
    }

    @Override
    public String process(String key) {
        if (this.recipe == null)
            return null;
        if (key.startsWith("input")) {
            int id = Integer.parseInt(key.substring(5)) - 1;
            if (this.recipe.ingredients.length > id)
                return PatchouliAPI.instance.serializeIngredient(this.recipe.ingredients[id]);
            else
                return null;
        } else {
            switch (key) {
                case "output":
                    return PatchouliAPI.instance.serializeItemStack(this.recipe.result);
                case "sapling":
                    return PatchouliAPI.instance.serializeIngredient(this.recipe.saplingType);
                case "name":
                    return this.recipe.result.getDisplayName();
                default:
                    return null;
            }
        }
    }
}
