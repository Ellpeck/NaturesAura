package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorOffering implements IComponentProcessor {

    private OfferingRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("offering", provider.get("recipe").asString());
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
            case "start":
                return IVariable.from(this.recipe.startItem);
            case "name":
                return IVariable.wrap(this.recipe.output.getDisplayName().getString());
            default:
                return null;
        }
    }
}
