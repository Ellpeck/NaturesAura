package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneBrick.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    public static boolean validateLoosely(Multiblock mb, World world, BlockPos pos, Block ignored) {
        return validateLoosely(mb, pos,
                (start, x, y, z, matcher) -> matcher.displayState.getBlock() == ignored || mb.test(world, start, x, y, z, Rotation.NONE));
    }

    public static boolean validateLoosely(Multiblock mb, BlockPos pos, LooseValidator validator) {
        BlockPos start = pos.add(-mb.offX, -mb.offY, -mb.offZ);
        for (int x = 0; x < mb.sizeX; x++)
            for (int y = 0; y < mb.sizeY; y++)
                for (int z = 0; z < mb.sizeZ; z++) {
                    if (!validator.works(start, x, y, z, mb.stateTargets[x][y][z])) {
                        return false;
                    }
                }
        return true;
    }

    private interface LooseValidator {
        boolean works(BlockPos start, int x, int y, int z, StateMatcher matcher);
    }
}
