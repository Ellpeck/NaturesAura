package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.AuraType;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ModRecipes {

    public static void init() {
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "eye"),
                new ItemStack(Blocks.SAPLING), new ItemStack(ModItems.EYE), 250,
                new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.GOLD_LEAF),
                new ItemStack(ModItems.GOLD_LEAF));
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "nature_altar"),
                new ItemStack(Blocks.SAPLING), new ItemStack(ModBlocks.NATURE_ALTAR), 500,
                new ItemStack(Blocks.STONE),
                new ItemStack(Blocks.STONE),
                new ItemStack(Blocks.STONE),
                new ItemStack(ModItems.GOLD_LEAF),
                new ItemStack(Items.GOLD_INGOT),
                ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), AuraType.OVERWORLD));
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "ancient_sapling"),
                new ItemStack(Blocks.SAPLING), new ItemStack(ModBlocks.ANCIENT_SAPLING), 200,
                new ItemStack(Blocks.SAPLING),
                new ItemStack(Blocks.YELLOW_FLOWER),
                new ItemStack(Blocks.RED_FLOWER),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(Items.REEDS),
                new ItemStack(ModItems.GOLD_LEAF));
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "furnace_heater"),
                new ItemStack(Blocks.SAPLING), new ItemStack(ModBlocks.FURNACE_HEATER), 600,
                new ItemStack(ModBlocks.INFUSED_STONE),
                new ItemStack(ModBlocks.INFUSED_STONE),
                new ItemStack(ModItems.INFUSED_IRON),
                new ItemStack(ModItems.INFUSED_IRON),
                new ItemStack(Items.FIRE_CHARGE),
                new ItemStack(Items.FLINT),
                new ItemStack(Blocks.MAGMA),
                ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), AuraType.NETHER));
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "conversion_catalyst"),
                new ItemStack(Blocks.SAPLING, 1, 3), new ItemStack(ModBlocks.CONVERSION_CATALYST), 600,
                new ItemStack(Blocks.STONEBRICK, 1, 1),
                new ItemStack(ModBlocks.INFUSED_STONE),
                new ItemStack(Items.BREWING_STAND),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.GOLD_LEAF),
                new ItemStack(Blocks.GLOWSTONE));

        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_iron"),
                new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.INFUSED_IRON),
                null, 300, 80);
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_stone"),
                new ItemStack(Blocks.STONE), new ItemStack(ModBlocks.INFUSED_STONE),
                null, 150, 40);

        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "chorus"),
                ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), AuraType.END), new ItemStack(Items.DRAGON_BREATH),
                ModBlocks.CONVERSION_CATALYST, 350, 80);
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "leather"),
                new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.LEATHER),
                ModBlocks.CONVERSION_CATALYST, 400, 50);
    }
}
