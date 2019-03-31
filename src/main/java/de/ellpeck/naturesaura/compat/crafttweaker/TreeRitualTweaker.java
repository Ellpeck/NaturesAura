package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ZenRegister
@ZenClass("mods." + NaturesAura.MOD_ID + ".TreeRitual")
public final class TreeRitualTweaker {

    @ZenMethod
    public static void addRecipe(String name, IIngredient saplingType, IItemStack result, int time, IIngredient[] items) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            ResourceLocation res = new ResourceLocation(Compat.CRAFT_TWEAKER, name);
            return new Add(Collections.singletonMap(res, new TreeRitualRecipe(res,
                    CraftTweakerMC.getIngredient(saplingType),
                    InputHelper.toStack(result),
                    time,
                    Arrays.stream(items).map(CraftTweakerMC::getIngredient).toArray(Ingredient[]::new)
            )));
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            Map<ResourceLocation, TreeRitualRecipe> recipes = new HashMap<>();
            for (TreeRitualRecipe recipe : NaturesAuraAPI.TREE_RITUAL_RECIPES.values())
                if (Helper.areItemsEqual(recipe.result, InputHelper.toStack(output), true))
                    recipes.put(recipe.name, recipe);
            return new Remove(recipes);
        });
    }

    private static class Add extends BaseMapAddition<ResourceLocation, TreeRitualRecipe> {

        protected Add(Map<ResourceLocation, TreeRitualRecipe> map) {
            super("Tree Ritual", NaturesAuraAPI.TREE_RITUAL_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, TreeRitualRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().result);
        }
    }

    private static class Remove extends BaseMapRemoval<ResourceLocation, TreeRitualRecipe> {

        protected Remove(Map<ResourceLocation, TreeRitualRecipe> map) {
            super("Tree Ritual", NaturesAuraAPI.TREE_RITUAL_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, TreeRitualRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().result);
        }
    }
}
