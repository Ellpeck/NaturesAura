package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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
        var helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(
                new TreeRitualCategory(helper),
                new AltarCategory(helper),
                new OfferingCategory(helper),
                new AnimalSpawnerCategory(helper)
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.EFFECT_POWDER, (stack, context) -> ItemEffectPowder.getEffect(stack).toString());
        registration.registerSubtypeInterpreter(ModItems.AURA_BOTTLE, (stack, context) -> ItemAuraBottle.getType(stack).getName().toString());

        var auraInterpreter = (IIngredientSubtypeInterpreter<ItemStack>) (stack, context) -> {
            var container = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER).orElse(null);
            if (container != null)
                return String.valueOf(container.getStoredAura());
            return IIngredientSubtypeInterpreter.NONE;
        };
        registration.registerSubtypeInterpreter(ModItems.AURA_CACHE, auraInterpreter);
        registration.registerSubtypeInterpreter(ModItems.AURA_TROVE, auraInterpreter);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_POWDER), TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WOOD_STAND), TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NATURE_ALTAR), ALTAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OFFERING_TABLE), OFFERING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANIMAL_SPAWNER), SPAWNER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(manager.getAllRecipesFor(ModRecipes.TREE_RITUAL_TYPE), TREE_RITUAL);
        registration.addRecipes(manager.getAllRecipesFor(ModRecipes.ALTAR_TYPE), ALTAR);
        registration.addRecipes(manager.getAllRecipesFor(ModRecipes.OFFERING_TYPE), OFFERING);
        registration.addRecipes(manager.getAllRecipesFor(ModRecipes.ANIMAL_SPAWNER_TYPE), SPAWNER);
    }
}
