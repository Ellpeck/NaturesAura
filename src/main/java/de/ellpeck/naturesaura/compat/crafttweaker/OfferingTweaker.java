package de.ellpeck.naturesaura.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import de.ellpeck.naturesaura.compat.Compat;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;

@ZenRegister
@ZenCodeType.Name("mods." + NaturesAura.MOD_ID + ".Offering")
public final class OfferingTweaker {

    @ZenCodeType.Method
    public static void addRecipe(String name, IIngredient input, int inputAmount, IIngredient startItem, IItemStack output) {
        ResourceLocation res = new ResourceLocation("crafttweaker", name);
        CraftTweakerAPI.apply(new AddAction<>(NaturesAuraAPI.OFFERING_RECIPES, res, new OfferingRecipe(res,
                new AmountIngredient(input.asVanillaIngredient(), inputAmount),
                startItem.asVanillaIngredient(),
                output.getInternal())));
    }

    @ZenCodeType.Method
    public static void removeRecipe(IItemStack output) {
        List<ResourceLocation> recipes = new ArrayList<>();
        for (OfferingRecipe recipe : NaturesAuraAPI.OFFERING_RECIPES.values())
            if (Helper.areItemsEqual(recipe.output, output.getInternal(), true))
                recipes.add(recipe.name);
        CraftTweakerAPI.apply(new RemoveAction(NaturesAuraAPI.OFFERING_RECIPES, recipes));
    }
}
