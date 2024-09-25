package de.ellpeck.naturesaura.misc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class ColoredBlockHelper {

    public static final List<Block> WOOL = ColoredBlockHelper.collectBlocks("wool");
    public static final List<Block> TERRACOTTA = ColoredBlockHelper.collectBlocks("terracotta");
    public static final List<Block> CONCRETE_POWDER = ColoredBlockHelper.collectBlocks("concrete_powder");
    public static final List<Block> CONCRETE = ColoredBlockHelper.collectBlocks("concrete");
    public static final List<Block> GLASS = ColoredBlockHelper.collectBlocks("stained_glass");
    public static final List<Block> GLASS_PANE = ColoredBlockHelper.collectBlocks("stained_glass_pane");
    public static final List<Block> CARPET = ColoredBlockHelper.collectBlocks("carpet");
    public static final List<List<Block>> LISTS = Arrays.asList(ColoredBlockHelper.WOOL, ColoredBlockHelper.TERRACOTTA, ColoredBlockHelper.CONCRETE_POWDER, ColoredBlockHelper.CONCRETE, ColoredBlockHelper.GLASS, ColoredBlockHelper.GLASS_PANE, ColoredBlockHelper.CARPET);

    private static List<Block> collectBlocks(String name) {
        return Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(c -> {
            var loc = ResourceLocation.parse(c.getName() + '_' + name);
            var block = BuiltInRegistries.BLOCK.get(loc);
            if (block == null || block == Blocks.AIR)
                throw new IllegalStateException("Couldn't find block with name " + loc);
            return block;
        }).toList();
    }

    public static List<Block> getBlocksContaining(Block block) {
        for (var list : ColoredBlockHelper.LISTS) {
            if (list.contains(block))
                return list;
        }
        return null;
    }

}
