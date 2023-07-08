package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AltarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorAltar implements IComponentProcessor {

    private AltarRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("altar", provider.get("recipe").asString());
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null)
            return null;
        return switch (key) {
            case "input" -> PatchouliCompat.ingredientVariable(this.recipe.input);
            case "output" -> IVariable.from(this.recipe.output);
            case "catalyst" ->
                    this.recipe.catalyst != Ingredient.EMPTY ? PatchouliCompat.ingredientVariable(this.recipe.catalyst) : null;
            case "name" -> IVariable.wrap(this.recipe.output.getHoverName().getString());
            default -> null;
        };
    }

    @Override
    public boolean allowRender(String group) {
        return group.isEmpty() || group.equals(this.recipe.catalyst == Ingredient.EMPTY ? "altar" : "catalyst");
    }
}
