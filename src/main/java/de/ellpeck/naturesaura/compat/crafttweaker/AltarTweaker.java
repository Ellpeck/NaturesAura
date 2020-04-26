package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;

@ZenRegister
@ZenCodeType.Name("mods." + NaturesAura.MOD_ID + ".Altar")
public final class AltarTweaker {

    @ZenCodeType.Method
    public static void addRecipe(String name, IIngredient input, IItemStack output, String auraType, int aura, int time, @ZenCodeType.Optional IIngredient catalyst) {
        ResourceLocation res = new ResourceLocation("crafttweaker", name);
        CraftTweakerAPI.apply(new AddAction<>(NaturesAuraAPI.ALTAR_RECIPES, res, new AltarRecipe(res,
                input.asVanillaIngredient(),
                output.getInternal(),
                NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(auraType)),
                catalyst == null ? Ingredient.EMPTY : catalyst.asVanillaIngredient(),
                aura, time)));
    }

    @ZenCodeType.Method
    public static void removeRecipe(IItemStack output) {
        List<ResourceLocation> recipes = new ArrayList<>();
        for (AltarRecipe recipe : NaturesAuraAPI.ALTAR_RECIPES.values())
            if (Helper.areItemsEqual(recipe.output, output.getInternal(), true))
                recipes.add(recipe.name);
        CraftTweakerAPI.apply(new RemoveAction(NaturesAuraAPI.ALTAR_RECIPES, recipes));
    }
}
