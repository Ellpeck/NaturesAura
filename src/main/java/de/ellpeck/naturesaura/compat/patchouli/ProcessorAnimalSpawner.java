package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorAnimalSpawner implements IComponentProcessor {

    private AnimalSpawnerRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("animal_spawner", provider.get("recipe", level.registryAccess()).asString());
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
                case "name" -> IVariable.wrap(this.recipe.entity.getDescription().getString(), level.registryAccess());
                case "entity" -> IVariable.wrap(BuiltInRegistries.ENTITY_TYPE.getKey(this.recipe.entity).toString(), level.registryAccess());
                case "egg" -> IVariable.from(new ItemStack(SpawnEggItem.byId(this.recipe.entity)), level.registryAccess());
                default -> null;
            };
        }
    }

    @Override
    public boolean allowRender(String group) {
        return !"seekrit".equals(group);
    }

}
