package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorTreeRitual implements IComponentProcessor {

    private TreeRitualRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("tree_ritual", provider.get("recipe", level.registryAccess()).asString());
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null)
            return null;
        if (key.startsWith("input")) {
            var id = Integer.parseInt(key.substring(5)) - 1;
            return this.recipe.ingredients.size() > id ? PatchouliCompat.ingredientVariable(this.recipe.ingredients.get(id), level.registryAccess()) : null;
        } else {
            return switch (key) {
                case "output" -> IVariable.from(this.recipe.output, level.registryAccess());
                case "sapling" -> PatchouliCompat.ingredientVariable(this.recipe.saplingType, level.registryAccess());
                case "name" -> IVariable.wrap(this.recipe.output.getHoverName().getString(), level.registryAccess());
                default -> null;
            };
        }
    }

}
