package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class ModRecipes {

    public static void init() {
        new TreeRitualRecipe(new ItemStack(Blocks.SAPLING), new ItemStack(ModItems.EYE), 250,
                new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(ModItems.GOLD_LEAF),
                new ItemStack(ModItems.GOLD_LEAF)).add();
        new TreeRitualRecipe(new ItemStack(Blocks.SAPLING), new ItemStack(ModBlocks.NATURE_ALTAR), 500,
                new ItemStack(Blocks.STONE),
                new ItemStack(Blocks.STONE),
                new ItemStack(Blocks.STONE),
                new ItemStack(ModItems.GOLD_LEAF),
                new ItemStack(Items.DIAMOND)).add();

        new AltarRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.INFUSED_IRON), 200, 30).add();
    }
}
