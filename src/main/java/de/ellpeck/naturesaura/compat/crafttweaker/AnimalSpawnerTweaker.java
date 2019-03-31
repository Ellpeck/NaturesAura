package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@ZenRegister
@ZenClass("mods." + NaturesAura.MOD_ID + ".AnimalSpawner")
public final class AnimalSpawnerTweaker {

    @ZenMethod
    public static void addRecipe(String name, String entity, int aura, int time, IIngredient[] ingredients) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            ResourceLocation res = new ResourceLocation(Compat.CRAFT_TWEAKER, name);
            return new Add(Collections.singletonMap(res, new AnimalSpawnerRecipe(res, new ResourceLocation(entity), aura, time,
                    Arrays.stream(ingredients).map(CraftTweakerMC::getIngredient).toArray(Ingredient[]::new)
            )));
        });
    }

    @ZenMethod
    public static void removeRecipe(String name) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            ResourceLocation res = new ResourceLocation(name);
            return new Remove(Collections.singletonMap(res, NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.get(res)));
        });
    }

    private static class Add extends BaseMapAddition<ResourceLocation, AnimalSpawnerRecipe> {

        protected Add(Map<ResourceLocation, AnimalSpawnerRecipe> map) {
            super("AnimalSpawner", NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, AnimalSpawnerRecipe> recipe) {
            return recipe.getValue().name.toString();
        }
    }

    private static class Remove extends BaseMapRemoval<ResourceLocation, AnimalSpawnerRecipe> {

        protected Remove(Map<ResourceLocation, AnimalSpawnerRecipe> map) {
            super("AnimalSpawner", NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, AnimalSpawnerRecipe> recipe) {
            return recipe.getValue().name.toString();
        }
    }
}
