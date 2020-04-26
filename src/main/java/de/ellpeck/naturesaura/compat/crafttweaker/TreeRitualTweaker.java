package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;

@ZenRegister
@ZenCodeType.Name("mods." + NaturesAura.MOD_ID + ".TreeRitual")
public final class TreeRitualTweaker {

    @ZenCodeType.Method
    public static void addRecipe(String name, IIngredient saplingType, IItemStack result, int time, IIngredient[] items) {
        ResourceLocation res = new ResourceLocation("crafttweaker", name);
        CraftTweakerAPI.apply(new AddAction<>(NaturesAuraAPI.TREE_RITUAL_RECIPES, res, new TreeRitualRecipe(res,
                saplingType.asVanillaIngredient(),
                result.getInternal(),
                time,
                Arrays.stream(items).map(IIngredient::asVanillaIngredient).toArray(Ingredient[]::new)
        )));
    }

    @ZenCodeType.Method
    public static void removeRecipe(IItemStack output) {
        List<ResourceLocation> recipes = new ArrayList<>();
        for (TreeRitualRecipe recipe : NaturesAuraAPI.TREE_RITUAL_RECIPES.values())
            if (Helper.areItemsEqual(recipe.result, output.getInternal(), true))
                recipes.add(recipe.name);
        CraftTweakerAPI.apply(new RemoveAction(NaturesAuraAPI.TREE_RITUAL_RECIPES, recipes));
    }
}
