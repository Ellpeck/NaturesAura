package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorTreeRitual implements IComponentProcessor {

    private TreeRitualRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        ResourceLocation res = new ResourceLocation(provider.get("recipe"));
        this.recipe = TreeRitualRecipe.RECIPES.get(res);
    }

    @Override
    public String process(String key) {
        if (key.startsWith("input")) {
            int id = Integer.parseInt(key.substring(5)) - 1;
            if (this.recipe.items.length > id)
                return PatchouliAPI.instance.serializeItemStack(this.recipe.items[id]);
            else
                return null;
        } else {
            switch (key) {
                case "output":
                    return PatchouliAPI.instance.serializeItemStack(this.recipe.result);
                case "sapling":
                    return PatchouliAPI.instance.serializeItemStack(this.recipe.saplingType);
                case "name":
                    return this.recipe.result.getDisplayName();
                default:
                    return null;
            }
        }
    }
}
