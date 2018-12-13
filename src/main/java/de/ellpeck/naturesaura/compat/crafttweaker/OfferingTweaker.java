package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ZenRegister
@ZenClass("mods." + NaturesAura.MOD_ID + ".Offering")
public final class OfferingTweaker {

    @ZenMethod
    public static void addRecipe(String name, IItemStack input, IItemStack startItem, IItemStack output) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            ResourceLocation res = new ResourceLocation(name);
            return new Add(Collections.singletonMap(res, new OfferingRecipe(res,
                    new AmountIngredient(InputHelper.toStack(input)),
                    Ingredient.fromStacks(InputHelper.toStack(startItem)),
                    InputHelper.toStack(output))));
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            Map<ResourceLocation, OfferingRecipe> recipes = new HashMap<>();
            for (OfferingRecipe recipe : NaturesAuraAPI.OFFERING_RECIPES.values())
                if (Helper.areItemsEqual(recipe.output, InputHelper.toStack(output), true))
                    recipes.put(recipe.name, recipe);
            return new Remove(recipes);
        });
    }

    private static class Add extends BaseMapAddition<ResourceLocation, OfferingRecipe> {

        protected Add(Map<ResourceLocation, OfferingRecipe> map) {
            super("Offering", NaturesAuraAPI.OFFERING_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, OfferingRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().output);
        }
    }

    private static class Remove extends BaseMapRemoval<ResourceLocation, OfferingRecipe> {

        protected Remove(Map<ResourceLocation, OfferingRecipe> map) {
            super("Tree Ritual", NaturesAuraAPI.OFFERING_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, OfferingRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().output);
        }
    }
}
