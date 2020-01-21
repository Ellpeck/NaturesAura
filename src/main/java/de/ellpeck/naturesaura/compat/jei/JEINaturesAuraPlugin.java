package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEINaturesAuraPlugin implements IModPlugin {

    public static final ResourceLocation TREE_RITUAL = new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual");
    public static final ResourceLocation ALTAR = new ResourceLocation(NaturesAura.MOD_ID, "altar");
    public static final ResourceLocation OFFERING = new ResourceLocation(NaturesAura.MOD_ID, "offering");
    public static final ResourceLocation SPAWNER = new ResourceLocation(NaturesAura.MOD_ID, "animal_spawner");

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(NaturesAura.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                new TreeRitualCategory(helper),
                new AltarCategory(helper),
                new OfferingCategory(helper),
                new AnimalSpawnerCategory(helper)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_POWDER), TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WOOD_STAND), TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NATURE_ALTAR), ALTAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CONVERSION_CATALYST), ALTAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OFFERING_TABLE), OFFERING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANIMAL_SPAWNER), SPAWNER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(NaturesAuraAPI.TREE_RITUAL_RECIPES.values(), TREE_RITUAL);
        registration.addRecipes(NaturesAuraAPI.ALTAR_RECIPES.values(), ALTAR);
        registration.addRecipes(NaturesAuraAPI.OFFERING_RECIPES.values(), OFFERING);
        registration.addRecipes(NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.values(), SPAWNER);
    }
}
