/* TODO crafttweaker or whatever
package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.minecraft.CraftTweakerMC;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ZenRegister
@Zen("mods." + NaturesAura.MOD_ID + ".Altar")
public final class AltarTweaker {

    @ZenMethod
    public static void addRecipe(String name, IIngredient input, IItemStack output, IIngredient catalyst, int aura, int time) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            ResourceLocation res = new ResourceLocation(Compat.CRAFT_TWEAKER, name);
            return new Add(Collections.singletonMap(res, new AltarRecipe(res,
                    CraftTweakerMC.getIngredient(input),
                    InputHelper.toStack(output),
                    CraftTweakerMC.getIngredient(catalyst),
                    aura, time)));
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        CraftTweakerCompat.SCHEDULED_ACTIONS.add(() -> {
            Map<ResourceLocation, AltarRecipe> recipes = new HashMap<>();
            for (AltarRecipe recipe : NaturesAuraAPI.ALTAR_RECIPES.values())
                if (Helper.areItemsEqual(recipe.output, InputHelper.toStack(output), true))
                    recipes.put(recipe.name, recipe);
            return new Remove(recipes);
        });
    }

    private static class Add extends BaseMapAddition<ResourceLocation, AltarRecipe> {

        protected Add(Map<ResourceLocation, AltarRecipe> map) {
            super("Natural Altar", NaturesAuraAPI.ALTAR_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, AltarRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().output);
        }
    }

    private static class Remove extends BaseMapRemoval<ResourceLocation, AltarRecipe> {

        protected Remove(Map<ResourceLocation, AltarRecipe> map) {
            super("Natural Altar", NaturesAuraAPI.ALTAR_RECIPES, map);
        }

        @Override
        protected String getRecipeInfo(Map.Entry<ResourceLocation, AltarRecipe> recipe) {
            return LogHelper.getStackDescription(recipe.getValue().output);
        }
    }
}
*/
