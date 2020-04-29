package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.recipes.AnimalSpawnerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAnimalSpawner implements IComponentProcessor {

    private AnimalSpawnerRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = PatchouliCompat.getRecipe("animal_spawner", provider.get("recipe"));
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
                case "name":
                    return this.recipe.entity.getName().getFormattedText();
                case "entity":
                    return this.recipe.entity.getRegistryName().toString();
                case "egg":
                    ItemStack egg = new ItemStack(SpawnEggItem.getEgg(this.recipe.entity));
                    return PatchouliAPI.instance.serializeItemStack(egg);
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean allowRender(String group) {
        return !"seekrit".equals(group);
    }
}
