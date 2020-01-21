package de.ellpeck.naturesaura.compat.patchouli;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.PatchouliAPI;

public class ProcessorAnimalSpawner implements IComponentProcessor {

    private AnimalSpawnerRecipe recipe;

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.recipe = PatchouliCompat.getRecipe(NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES, provider.get("recipe"));
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
            EntityType entry = ForgeRegistries.ENTITIES.getValue(this.recipe.entity);
            switch (key) {
                case "name":
                    return I18n.format("entity." + entry.getName() + ".name");
                case "entity":
                    return this.recipe.entity.toString();
                case "egg":
                    ItemStack egg = new ItemStack(SpawnEggItem.getEgg(entry));
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
