package de.ellpeck.naturesaura.compat.jei;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JEINaturesAuraPlugin implements IModPlugin {

    public static final RecipeType<TreeRitualRecipe> TREE_RITUAL = RecipeType.create(NaturesAura.MOD_ID, "tree_ritual", TreeRitualRecipe.class);
    public static final RecipeType<AltarRecipe> ALTAR = RecipeType.create(NaturesAura.MOD_ID, "altar", AltarRecipe.class);
    public static final RecipeType<OfferingRecipe> OFFERING = RecipeType.create(NaturesAura.MOD_ID, "offering", OfferingRecipe.class);
    public static final RecipeType<AnimalSpawnerRecipe> SPAWNER = RecipeType.create(NaturesAura.MOD_ID, "animal_spawner", AnimalSpawnerRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "jei_plugin");
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
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EFFECT_POWDER, new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return ItemEffectPowder.getEffect(ingredient);
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return ItemEffectPowder.getEffect(ingredient).toString();
            }
        });
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AURA_BOTTLE, new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return ItemAuraBottle.getType(ingredient).getName();
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                return ItemAuraBottle.getType(ingredient).getName().toString();
            }
        });

        var auraInterpreter = new ISubtypeInterpreter<ItemStack>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                var container = ingredient.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
                return container != null ? container.getStoredAura() : null;
            }

            @Override
            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                var container = ingredient.getCapability(NaturesAuraAPI.AURA_CONTAINER_ITEM_CAPABILITY);
                return container != null ? String.valueOf(container.getStoredAura()) : "";
            }
        };
        registration.registerSubtypeInterpreter(ModItems.AURA_CACHE, auraInterpreter);
        registration.registerSubtypeInterpreter(ModItems.AURA_TROVE, auraInterpreter);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.GOLD_POWDER), JEINaturesAuraPlugin.TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WOOD_STAND), JEINaturesAuraPlugin.TREE_RITUAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NATURE_ALTAR), JEINaturesAuraPlugin.ALTAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OFFERING_TABLE), JEINaturesAuraPlugin.OFFERING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANIMAL_SPAWNER), JEINaturesAuraPlugin.SPAWNER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var manager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(JEINaturesAuraPlugin.TREE_RITUAL, manager.getAllRecipesFor(ModRecipes.TREE_RITUAL_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(JEINaturesAuraPlugin.ALTAR, manager.getAllRecipesFor(ModRecipes.ALTAR_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(JEINaturesAuraPlugin.OFFERING, manager.getAllRecipesFor(ModRecipes.OFFERING_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(JEINaturesAuraPlugin.SPAWNER, manager.getAllRecipesFor(ModRecipes.ANIMAL_SPAWNER_TYPE).stream().map(RecipeHolder::value).toList());
    }

}
