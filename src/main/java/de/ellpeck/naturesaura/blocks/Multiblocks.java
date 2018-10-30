package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Predicate;

public final class Multiblocks {

    public static final IMultiblock ALTAR = make(
            new ResourceLocation(NaturesAura.MOD_ID, "altar"),
            new String[][]{
                    {"    M    ", "         ", "         ", "         ", "M       M", "         ", "         ", "         ", "    M    "},
                    {"    B    ", "         ", "         ", "         ", "B       B", "         ", "         ", "         ", "    B    "},
                    {"    B    ", "         ", "  M   M  ", "         ", "B   0   B", "         ", "  M   M  ", "         ", "    B    "},
                    {"         ", "   WBW   ", "   WBW   ", " WWCWCWW ", " BBW WBB ", " WWCWCWW ", "   WBW   ", "   WBW   ", "         "}},
            'C', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.CHISELED),
            'B', Blocks.STONEBRICK.getDefaultState(),
            'W', Blocks.PLANKS,
            'M', Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.MOSSY),
            '0', ModBlocks.NATURE_ALTAR,
            ' ', anyMatcher()
    ).setSymmetrical(true);
    public static final IMultiblock TREE_RITUAL = make(
            new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"),
            new String[][]{
                    {"    W    ", " W     W ", "   GGG   ", "  GG GG  ", "W G 0 G W", "  GG GG  ", "   GGG   ", " W     W ", "    W    "}},
            'W', ModBlocks.WOOD_STAND,
            'G', ModBlocks.GOLD_POWDER,
            '0', matcher(Blocks.SAPLING, state -> state.getBlock() instanceof BlockSapling || state.getBlock() instanceof BlockLog),
            ' ', anyMatcher()
    ).setSymmetrical(true);
    public static final IMultiblock POTION_GENERATOR = make(
            new ResourceLocation(NaturesAura.MOD_ID, "potion_generator"),
            new String[][]{
                    {"R     R", "       ", "       ", "       ", "       ", "       ", "R     R"},
                    {"N     N", "       ", "       ", "       ", "       ", "       ", "N     N"},
                    {"N     N", "       ", "       ", "   0   ", "       ", "       ", "N     N"},
                    {" N   N ", "NNN NNN", " NRRRN ", "  R R  ", " NRRRN ", "NNN NNN", " N   N "}},
            'N', Blocks.NETHER_BRICK,
            'R', Blocks.RED_NETHER_BRICK,
            '0', ModBlocks.POTION_GENERATOR,
            ' ', anyMatcher()
    ).setSymmetrical(true);

    private static IStateMatcher anyMatcher() {
        return PatchouliAPI.instance.anyMatcher();
    }

    private static IStateMatcher matcher(Block block, Predicate<IBlockState> predicate) {
        return PatchouliAPI.instance.predicateMatcher(block, predicate);
    }

    private static IMultiblock make(ResourceLocation res, String[][] pattern, Object... targets) {
        IMultiblock multi = PatchouliAPI.instance.makeMultiblock(pattern, targets);
        PatchouliAPI.instance.registerMultiblock(res, multi);
        return multi;
    }
}
