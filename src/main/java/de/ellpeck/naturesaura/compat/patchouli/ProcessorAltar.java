package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAltar implements IComponentProcessor {

    private AltarRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = PatchouliCompat.getRecipe(NaturesAuraAPI.ALTAR_RECIPES, provider.get("recipe"));
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
            case "catalyst":
                if (this.recipe.catalyst != Ingredient.EMPTY)
                    return PatchouliAPI.instance.serializeIngredient(this.recipe.catalyst);
                else
                    return null;
            case "name":
                return this.recipe.output.getDisplayName().getFormattedText();
            default:
                return null;
        }
    }

    @Override
    public boolean allowRender(String group) {
        return group.isEmpty() || group.equals(this.recipe.catalyst == Ingredient.EMPTY ? "altar" : "catalyst");
    }
}
