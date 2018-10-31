package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AltarRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAltar implements IComponentProcessor {

    private AltarRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        ResourceLocation res = new ResourceLocation(provider.get("recipe"));
        this.recipe = AltarRecipe.RECIPES.get(res);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "input":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.input);
            case "output":
                return PatchouliAPI.instance.serializeItemStack(this.recipe.output);
            case "catalyst":
                if (this.recipe.catalyst != null)
                    return PatchouliAPI.instance.serializeItemStack(new ItemStack(this.recipe.catalyst));
                else
                    return null;
            case "name":
                return this.recipe.output.getDisplayName();
            default:
                return null;
        }
    }

    @Override
    public boolean allowRender(String group) {
        return group.isEmpty() || group.equals(this.recipe.catalyst == null ? "altar" : "catalyst");
    }
}
