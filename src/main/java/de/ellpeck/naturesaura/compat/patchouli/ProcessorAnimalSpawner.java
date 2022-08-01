package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ProcessorAnimalSpawner implements IComponentProcessor {

    private AnimalSpawnerRecipe recipe;

    @Override
    public void setup(IVariableProvider provider) {
        this.recipe = PatchouliCompat.getRecipe("animal_spawner", provider.get("recipe").asString());
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null)
            return null;
        if (key.startsWith("input")) {
            var id = Integer.parseInt(key.substring(5)) - 1;
            return this.recipe.ingredients.length > id ? PatchouliCompat.ingredientVariable(this.recipe.ingredients[id]) : null;
        } else {
            return switch (key) {
                case "name" -> IVariable.wrap(this.recipe.entity.getDescription().getString());
                case "entity" -> IVariable.wrap(ForgeRegistries.ENTITY_TYPES.getKey(this.recipe.entity).toString());
                case "egg" -> IVariable.from(new ItemStack(ForgeSpawnEggItem.fromEntityType(this.recipe.entity)));
                default -> null;
            };
        }
    }

    @Override
    public boolean allowRender(String group) {
        return !"seekrit".equals(group);
    }
}
