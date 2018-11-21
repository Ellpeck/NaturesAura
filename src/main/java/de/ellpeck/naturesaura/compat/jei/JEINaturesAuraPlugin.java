package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.compat.jei.altar.AltarCategory;
import de.ellpeck.naturesaura.compat.jei.altar.AltarWrapper;
import de.ellpeck.naturesaura.compat.jei.offering.OfferingCategory;
import de.ellpeck.naturesaura.compat.jei.offering.OfferingWrapper;
import de.ellpeck.naturesaura.compat.jei.treeritual.TreeRitualCategory;
import de.ellpeck.naturesaura.compat.jei.treeritual.TreeRitualWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEINaturesAuraPlugin implements IModPlugin {

    public static final String TREE_RITUAL = NaturesAura.MOD_ID + ".tree_ritual";
    public static final String ALTAR = NaturesAura.MOD_ID + ".altar";
    public static final String OFFERING = NaturesAura.MOD_ID + ".offering";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                new TreeRitualCategory(helper),
                new AltarCategory(helper),
                new OfferingCategory(helper)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(TreeRitualRecipe.class, TreeRitualWrapper::new, TREE_RITUAL);
        registry.handleRecipes(AltarRecipe.class, AltarWrapper::new, ALTAR);
        registry.handleRecipes(OfferingRecipe.class, OfferingWrapper::new, OFFERING);

        registry.addRecipes(NaturesAuraAPI.TREE_RITUAL_RECIPES.values(), TREE_RITUAL);
        registry.addRecipes(NaturesAuraAPI.ALTAR_RECIPES.values(), ALTAR);
        registry.addRecipes(NaturesAuraAPI.OFFERING_RECIPES.values(), OFFERING);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_POWDER), TREE_RITUAL);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.WOOD_STAND), TREE_RITUAL);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.NATURE_ALTAR), ALTAR);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.CONVERSION_CATALYST), ALTAR);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.OFFERING_TABLE), OFFERING);
    }
}
