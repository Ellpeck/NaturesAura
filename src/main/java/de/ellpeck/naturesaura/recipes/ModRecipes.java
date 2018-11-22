package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import de.ellpeck.naturesaura.api.recipes.ing.NBTIngredient;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ModRecipes {

    public static void init() {
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "eye"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING)), new ItemStack(ModItems.EYE), 250,
                Ingredient.fromItem(Items.SPIDER_EYE),
                Ingredient.fromItem(Items.GOLD_INGOT),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Ingredient.fromItem(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "nature_altar"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.NATURE_ALTAR), 500,
                Helper.blockIng(Blocks.STONE),
                Helper.blockIng(Blocks.STONE),
                Helper.blockIng(Blocks.STONE),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Ingredient.fromItem(Items.GOLD_INGOT),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_OVERWORLD))).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "ancient_sapling"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.ANCIENT_SAPLING), 200,
                Helper.blockIng(Blocks.SAPLING),
                Helper.blockIng(Blocks.YELLOW_FLOWER),
                Helper.blockIng(Blocks.RED_FLOWER),
                Ingredient.fromItem(Items.WHEAT_SEEDS),
                Ingredient.fromItem(Items.REEDS),
                Ingredient.fromItem(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "furnace_heater"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.FURNACE_HEATER), 600,
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Ingredient.fromItem(ModItems.INFUSED_IRON),
                Ingredient.fromItem(ModItems.INFUSED_IRON),
                Ingredient.fromItem(Items.FIRE_CHARGE),
                Ingredient.fromItem(Items.FLINT),
                Helper.blockIng(Blocks.MAGMA),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_NETHER))).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "conversion_catalyst"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 3)), new ItemStack(ModBlocks.CONVERSION_CATALYST), 600,
                Ingredient.fromStacks(new ItemStack(Blocks.STONEBRICK, 1, 1)),
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Ingredient.fromItem(Items.BREWING_STAND),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Helper.blockIng(Blocks.GLOWSTONE)).register();

        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_iron"),
                Ingredient.fromItem(Items.IRON_INGOT), new ItemStack(ModItems.INFUSED_IRON),
                Ingredient.EMPTY, 300, 80).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_iron_block"),
                Helper.blockIng(Blocks.IRON_BLOCK), new ItemStack(ModBlocks.INFUSED_IRON),
                Ingredient.EMPTY, 2700, 700).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_stone"),
                Helper.blockIng(Blocks.STONE), new ItemStack(ModBlocks.INFUSED_STONE),
                Ingredient.EMPTY, 150, 40).register();

        Ingredient conversion = Helper.blockIng(ModBlocks.CONVERSION_CATALYST);
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "breath"),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_END)),
                new ItemStack(Items.DRAGON_BREATH),
                conversion, 500, 80).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "leather"),
                Ingredient.fromItem(Items.ROTTEN_FLESH), new ItemStack(Items.LEATHER),
                conversion, 400, 50).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "soul_sand"),
                Helper.blockIng(Blocks.SAND), new ItemStack(Blocks.SOUL_SAND),
                conversion, 200, 100).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "nether_wart"),
                Helper.blockIng(Blocks.RED_MUSHROOM), new ItemStack(Items.NETHER_WART),
                conversion, 600, 250).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "prismarine"),
                Ingredient.fromItem(Items.QUARTZ), new ItemStack(Items.PRISMARINE_SHARD),
                conversion, 850, 200).register();

        new OfferingRecipe(new ResourceLocation(NaturesAura.MOD_ID, "sky_ingot"),
                new AmountIngredient(new ItemStack(ModItems.INFUSED_IRON, 3)),
                Ingredient.fromItem(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.SKY_INGOT)).register();

        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE.getDefaultState(),
                Blocks.MOSSY_COBBLESTONE.getDefaultState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT),
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY));

        for (Block block : ForgeRegistries.BLOCKS)
            if (block instanceof BlockFlower)
                NaturesAuraAPI.FLOWERS.addAll(block.getBlockState().getValidStates());
    }
}
