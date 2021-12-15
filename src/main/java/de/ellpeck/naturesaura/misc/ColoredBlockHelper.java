package de.ellpeck.naturesaura.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ColoredBlockHelper {

    public static final List<Block> WOOL = collectBlocks("wool");
    public static final List<Block> TERRACOTTA = collectBlocks("terracotta");
    public static final List<Block> CONCRETE_POWDER = collectBlocks("concrete_powder");
    public static final List<Block> CONCRETE = collectBlocks("concrete");
    public static final List<Block> GLASS = collectBlocks("stained_glass");
    public static final List<Block> GLASS_PANE = collectBlocks("glass_pane");
    public static final List<Block> CARPET = collectBlocks("carpet");
    public static final List<List<Block>> LISTS = Arrays.asList(WOOL, TERRACOTTA, CONCRETE_POWDER, CONCRETE, GLASS, GLASS_PANE, CARPET);

    private static List<Block> collectBlocks(String name) {
        List<Block> blocks = new ArrayList<>();
        for (var color : DyeColor.values())
            blocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(color.getName() + '_' + name)));
        return Collections.unmodifiableList(blocks);
    }

    public static List<Block> getBlocksContaining(Block block) {
        for (var list : LISTS) {
            if (list.contains(block))
                return list;
        }
        return null;
    }
}
