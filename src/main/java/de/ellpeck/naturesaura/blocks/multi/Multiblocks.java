package de.ellpeck.naturesaura.blocks.multi;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class Multiblocks {

    public static final IMultiblock ALTAR = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "altar"),
            new String[][]{
                    {"    M    ", "         ", "         ", "         ", "M       M", "         ", "         ", "         ", "    M    "},
                    {"    B    ", "         ", "         ", "         ", "B       B", "         ", "         ", "         ", "    B    "},
                    {"    B    ", "         ", "  M   M  ", "         ", "B   0   B", "         ", "  M   M  ", "         ", "    B    "},
                    {"         ", "   WBW   ", "   WBW   ", " WWCWCWW ", " BBW WBB ", " WWCWCWW ", "   WBW   ", "   WBW   ", "         "}},
            'C', Blocks.CHISELED_STONE_BRICKS,
            'B', Blocks.STONE_BRICKS,
            'W', Blocks.OAK_PLANKS, // TODO create a matcher that matches by tag for planks
            'M', ModBlocks.GOLD_BRICK,
            '0', ModBlocks.NATURE_ALTAR,
            ' ', Matcher.wildcard());
    public static final IMultiblock TREE_RITUAL = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"),
            new String[][]{
                    {"    W    ", " W     W ", "   GGG   ", "  GG GG  ", "W G 0 G W", "  GG GG  ", "   GGG   ", " W     W ", "    W    "}},
            'W', new Matcher(ModBlocks.WOOD_STAND.getDefaultState(),
                    (world, start, offset, pos, state, c) -> world != null || state.getBlock() == ModBlocks.WOOD_STAND),
            'G', ModBlocks.GOLD_POWDER,
            '0', new Matcher(Blocks.OAK_SAPLING.getDefaultState(),
                    (world, start, offset, pos, state, c) -> {
                        if (state.getBlock() instanceof SaplingBlock || state.getBlock() instanceof LogBlock)
                            return true;
                        // try-catch to prevent blocks that need to have been placed crashing here
                        try {
                            ItemStack stack = state.getBlock().getItem(world, pos, state);
                            return !stack.isEmpty() && NaturesAuraAPI.TREE_RITUAL_RECIPES.values().stream().anyMatch(recipe -> recipe.saplingType.test(stack));
                        } catch (Exception e) {
                            return false;
                        }
                    }
            ),
            ' ', Matcher.wildcard());
    public static final IMultiblock POTION_GENERATOR = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "potion_generator"),
            new String[][]{
                    {"R     R", "       ", "       ", "       ", "       ", "       ", "R     R"},
                    {"N     N", "       ", "       ", "       ", "       ", "       ", "N     N"},
                    {"N     N", "       ", "       ", "   0   ", "       ", "       ", "N     N"},
                    {" N   N ", "NNN NNN", " NRRRN ", "  R R  ", " NRRRN ", "NNN NNN", " N   N "}},
            'N', Blocks.NETHER_BRICKS,
            'R', Blocks.RED_NETHER_BRICKS,
            '0', ModBlocks.POTION_GENERATOR,
            ' ', Matcher.wildcard());
    public static final IMultiblock OFFERING_TABLE = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "offering_table"),
            new String[][]{
                    {"  RRRRR  ", " R     R ", "R  RRR  R", "R R   R R", "R R 0 R R", "R R   R R", "R  RRR  R", " R     R ", "  RRRRR  "}},
            'R', new Matcher(Blocks.POPPY.getDefaultState(),
                    (world, start, offset, pos, state, c) -> NaturesAuraAPI.FLOWERS.contains(state)),
            '0', ModBlocks.OFFERING_TABLE,
            ' ', Matcher.wildcard());
    public static final IMultiblock ANIMAL_SPAWNER = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "animal_spawner"),
            new String[][]{
                    {"       ", "       ", "       ", "   0   ", "       ", "       ", "       "},
                    {"  HHH  ", " HRRRH ", "HRWRWRH", "HRR RRH", "HRWRWRH", " HRRRH ", "  HHH  "}},
            'H', Blocks.HAY_BLOCK,
            'R', ModBlocks.INFUSED_BRICK,
            'W', ModBlocks.ANCIENT_PLANKS,
            '0', ModBlocks.ANIMAL_SPAWNER,
            ' ', Matcher.wildcard());
    public static final IMultiblock AUTO_CRAFTER = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "auto_crafter"),
            new String[][]{
                    {"PPPPPPP", "PLPLPLP", "PPPPPPP", "PLP0PLP", "PPPPPPP", "PLPLPLP", "PPPPPPP"}},
            'P', ModBlocks.ANCIENT_PLANKS,
            'L', ModBlocks.ANCIENT_LOG,
            '0', ModBlocks.AUTO_CRAFTER,
            ' ', Matcher.wildcard());
    public static final IMultiblock RF_CONVERTER = ModConfig.enabledFeatures.rfConverter ? NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "rf_converter"),
            new String[][]{
                    {"       ", "       ", "       ", "   R   ", "       ", "       ", "       "},
                    {"       ", "   R   ", "       ", " R   R ", "       ", "   R   ", "       "},
                    {"       ", "       ", "       ", "       ", "       ", "       ", "       "},
                    {"   R   ", " R   R ", "       ", "R  0  R", "       ", " R   R ", "   R   "},
                    {"       ", "       ", "       ", "       ", "       ", "       ", "       "},
                    {"       ", "   R   ", "       ", " R   R ", "       ", "   R   ", "       "},
                    {"       ", "       ", "       ", "   R   ", "       ", "       ", "       "}},
            'R', Blocks.REDSTONE_BLOCK,
            '0', ModBlocks.RF_CONVERTER,
            ' ', Matcher.wildcard()) : null;
}
