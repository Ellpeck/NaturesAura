package de.ellpeck.naturesaura.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.jei.altar.AltarCategory;
import de.ellpeck.naturesaura.jei.altar.AltarWrapper;
import de.ellpeck.naturesaura.jei.treeritual.TreeRitualCategory;
import de.ellpeck.naturesaura.jei.treeritual.TreeRitualWrapper;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
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

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                new TreeRitualCategory(helper),
                new AltarCategory(helper)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(TreeRitualRecipe.class, TreeRitualWrapper::new, TREE_RITUAL);
        registry.handleRecipes(AltarRecipe.class, AltarWrapper::new, ALTAR);

        registry.addRecipes(TreeRitualRecipe.RECIPES, TREE_RITUAL);
        registry.addRecipes(AltarRecipe.RECIPES, ALTAR);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_POWDER), TREE_RITUAL);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.WOOD_STAND), TREE_RITUAL);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.NATURE_ALTAR), ALTAR);
    }
}
