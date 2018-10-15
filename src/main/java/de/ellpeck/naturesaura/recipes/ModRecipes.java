package de.ellpeck.naturesaura.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class ModRecipes {

    public static void init() {
        new TreeRitualRecipe(new ItemStack(Blocks.SAPLING), new ItemStack(Items.APPLE, 16), 300, new ItemStack(Items.BEETROOT), new ItemStack(Items.ITEM_FRAME), new ItemStack(Items.COMMAND_BLOCK_MINECART)).add();
    }
}
