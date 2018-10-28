package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.multiblock.Multiblock;
import vazkii.patchouli.common.multiblock.Multiblock.StateMatcher;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;

public final class Multiblocks {

    public static final Multiblock ALTAR = MultiblockRegistry.registerMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "altar"),
            new Multiblock(new String[][]{
                    {"    M    ", "         ", "         ", "         ", "M       M", "         ", "         ", "         ", "    M    "},
                    {"    B    ", "         ", "         ", "         ", "B       B", "         ", "         ", "         ", "    B    "},
                    {"    B    ", "         ", "  M   M  ", "         ", "B   0   B", "         ", "  M   M  ", "         ", "    B    "},
                    {"         ", "   WBW   ", "   WBW   ", " WWCWCWW ", " BBW WBB ", " WWCWCWW ", "   WBW   ", "   WBW   ", "         "}},
                    'C', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.CHISELED),
                    'B', Blocks.STONEBRICK.getDefaultState(),
                    'W', Blocks.PLANKS,
                    'M', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.MOSSY),
                    '0', ModBlocks.NATURE_ALTAR,
                    ' ', StateMatcher.ANY)
    ).setSymmetrical(true);
    public static final Multiblock TREE_RITUAL = MultiblockRegistry.registerMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"),
            new Multiblock(new String[][]{
                    {"    W    ", " W     W ", "   GGG   ", "  GG GG  ", "W G 0 G W", "  GG GG  ", "   GGG   ", " W     W ", "    W    "}},
                    'W', ModBlocks.WOOD_STAND,
                    'G', ModBlocks.GOLD_POWDER,
                    '0', StateMatcher.fromPredicate(Blocks.SAPLING, state -> state.getBlock() instanceof BlockSapling || state.getBlock() instanceof BlockLog),
                    ' ', StateMatcher.ANY)
    ).setSymmetrical(true);
    public static final Multiblock POTION_GENERATOR = MultiblockRegistry.registerMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "potion_generator"),
            new Multiblock(new String[][]{
                    {"R     R", "       ", "       ", "       ", "       ", "       ", "R     R"},
                    {"N     N", "       ", "       ", "       ", "       ", "       ", "N     N"},
                    {"N     N", "       ", "       ", "   0   ", "       ", "       ", "N     N"},
                    {" N   N ", "NNN NNN", " NRRRN ", "  R R  ", " NRRRN ", "NNN NNN", " N   N "}},
                    'N', Blocks.NETHER_BRICK,
                    'R', Blocks.RED_NETHER_BRICK,
                    '0', ModBlocks.POTION_GENERATOR,
                    ' ', StateMatcher.ANY)
    ).setSymmetrical(true);
}
