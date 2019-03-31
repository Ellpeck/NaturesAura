package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorOffering implements IComponentProcessor {

    private OfferingRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = PatchouliCompat.getRecipe(NaturesAuraAPI.OFFERING_RECIPES, provider.get("recipe"));
    }

    @Override
    public String process(String key) {
        if (this.recipe == null)
            return null;
        switch (key) {
            case "input":
                return PatchouliAPI.instance.serializeIngredient(this.recipe.input);
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.output);
            case "start":
                return PatchouliAPI.instance.serializeIngredient(this.recipe.startItem);
            case "name":
                return this.recipe.output.getDisplayName();
            default:
                return null;
        }
    }
}
