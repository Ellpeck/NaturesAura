package de.ellpeck.naturesaura.blocks.multi;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.multi.Multiblock.Matcher;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public final class Multiblocks {

    public static final Multiblock ALTAR = new Multiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "altar"),
            new String[][]{
                    {"    M    ", "         ", "         ", "         ", "M       M", "         ", "         ", "         ", "    M    "},
                    {"    B    ", "         ", "         ", "         ", "B       B", "         ", "         ", "         ", "    B    "},
                    {"    B    ", "         ", "  M   M  ", "         ", "B   0   B", "         ", "  M   M  ", "         ", "    B    "},
                    {"         ", "   WBW   ", "   WBW   ", " WWCWCWW ", " BBW WBB ", " WWCWCWW ", "   WBW   ", "   WBW   ", "         "}},
            'C', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.CHISELED),
            'B', Blocks.STONEBRICK.getDefaultState(),
            'W', Matcher.oreDict(Blocks.PLANKS, "plankWood"),
            'M', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.MOSSY),
            '0', ModBlocks.NATURE_ALTAR,
            ' ', Matcher.wildcard());
    public static final Multiblock TREE_RITUAL = new Multiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"),
            new String[][]{
                    {"    W    ", " W     W ", "   GGG   ", "  GG GG  ", "W G 0 G W", "  GG GG  ", "   GGG   ", " W     W ", "    W    "}},
            'W', new Matcher(ModBlocks.WOOD_STAND.getDefaultState(),
            (world, start, offset, pos, state, c) -> world != null || state.getBlock() == ModBlocks.WOOD_STAND),
            'G', ModBlocks.GOLD_POWDER,
            '0', new Matcher(Blocks.SAPLING.getDefaultState(),
            (world, start, offset, pos, state, c) -> state.getBlock() instanceof BlockSapling || state.getBlock() instanceof BlockLog),
            ' ', Matcher.wildcard());
    public static final Multiblock POTION_GENERATOR = new Multiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "potion_generator"),
            new String[][]{
                    {"R     R", "       ", "       ", "       ", "       ", "       ", "R     R"},
                    {"N     N", "       ", "       ", "       ", "       ", "       ", "N     N"},
                    {"N     N", "       ", "       ", "   0   ", "       ", "       ", "N     N"},
                    {" N   N ", "NNN NNN", " NRRRN ", "  R R  ", " NRRRN ", "NNN NNN", " N   N "}},
            'N', Blocks.NETHER_BRICK,
            'R', Blocks.RED_NETHER_BRICK,
            '0', ModBlocks.POTION_GENERATOR,
            ' ', Matcher.wildcard());
    public static final Multiblock OFFERING_TABLE = new Multiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "offering_table"),
            new String[][]{
                    {"  RRRRR  ", " R     R ", "R  RRR  R", "R R   R R", "R R 0 R R", "R R   R R", "R  RRR  R", " R     R ", "  RRRRR  "}},
            'R', new Matcher(Blocks.RED_FLOWER.getDefaultState(),
            (world, start, offset, pos, state, c) -> NaturesAuraAPI.FLOWERS.contains(state)),
            '0', ModBlocks.OFFERING_TABLE,
            ' ', Matcher.wildcard());
}
