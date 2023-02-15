package de.ellpeck.naturesaura.blocks.multi;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.data.BlockTagProvider;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.material.Material;

public final class Multiblocks {

    public static final IMultiblock ALTAR = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "altar"),
            new String[][]{
                    {"    M    ", "         ", "         ", "         ", "M       M", "         ", "         ", "         ", "    M    "},
                    {"    B    ", "         ", "         ", "         ", "B       B", "         ", "         ", "         ", "    B    "},
                    {"    B    ", "         ", "  M   M  ", "         ", "B   0   B", "         ", "  M   M  ", "         ", "    B    "},
                    {"         ", "   WBW   ", "   WBW   ", " WWCWCWW ", " BBW WBB ", " WWCWCWW ", "   WBW   ", "   WBW   ", "         "}},
            'C', Matcher.tag(Blocks.CHISELED_STONE_BRICKS, BlockTagProvider.ALTAR_FANCY_BRICK),
            'B', Matcher.tag(Blocks.STONE_BRICKS, BlockTagProvider.ALTAR_STONE),
            'W', Matcher.tag(Blocks.OAK_PLANKS, BlockTagProvider.ALTAR_WOOD),
            'M', Matcher.tag(ModBlocks.GOLD_BRICK, BlockTagProvider.ALTAR_GOLD_BRICK),
            '0', ModBlocks.NATURE_ALTAR,
            ' ', Matcher.any());
    public static final IMultiblock TREE_RITUAL = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"),
            new String[][]{
                    {"    W    ", " W     W ", "   GGG   ", "  GG GG  ", "W G 0 G W", "  GG GG  ", "   GGG   ", " W     W ", "    W    "}},
            'W', new Matcher(ModBlocks.WOOD_STAND.defaultBlockState(),
                    (level, start, offset, pos, state, c) -> level != null || state.getBlock() == ModBlocks.WOOD_STAND),
            'G', ModBlocks.GOLD_POWDER,
            '0', new Matcher(Blocks.OAK_SAPLING.defaultBlockState(),
                    (level, start, offset, pos, state, c) -> {
                        if (state.getBlock() instanceof SaplingBlock || state.getMaterial() == Material.WOOD)
                            return true;
                        // try-catch to prevent blocks that need to have been placed crashing here
                        try {
                            var stack = new ItemStack(state.getBlock());
                            return !stack.isEmpty() && level.getRecipeManager().getRecipesFor(ModRecipes.TREE_RITUAL_TYPE, null, null).stream().anyMatch(r -> r.saplingType.test(stack));
                        } catch (Exception e) {
                            return false;
                        }
                    }
            ),
            ' ', Matcher.any());
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
            ' ', Matcher.any());
    public static final IMultiblock OFFERING_TABLE = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "offering_table"),
            new String[][]{
                    {"  RRRRR  ", " R     R ", "R  RRR  R", "R R   R R", "R R 0 R R", "R R   R R", "R  RRR  R", " R     R ", "  RRRRR  "}},
            'R', new Matcher(Blocks.POPPY.defaultBlockState(),
                    (level, start, offset, pos, state, c) -> state.is(BlockTags.SMALL_FLOWERS)),
            '0', ModBlocks.OFFERING_TABLE,
            ' ', Matcher.any());
    public static final IMultiblock ANIMAL_SPAWNER = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "animal_spawner"),
            new String[][]{
                    {"       ", "       ", "       ", "   0   ", "       ", "       ", "       "},
                    {"  HHH  ", " HRRRH ", "HRWRWRH", "HRR RRH", "HRWRWRH", " HRRRH ", "  HHH  "}},
            'H', Blocks.HAY_BLOCK,
            'R', ModBlocks.INFUSED_BRICK,
            'W', ModBlocks.ANCIENT_PLANKS,
            '0', ModBlocks.ANIMAL_SPAWNER,
            ' ', Matcher.any());
    public static final IMultiblock AUTO_CRAFTER = NaturesAuraAPI.instance().createMultiblock(
            new ResourceLocation(NaturesAura.MOD_ID, "auto_crafter"),
            new String[][]{
                    {"PPPPPPP", "PLPLPLP", "PPPPPPP", "PLP0PLP", "PPPPPPP", "PLPLPLP", "PPPPPPP"}},
            'P', ModBlocks.ANCIENT_PLANKS,
            'L', ModBlocks.ANCIENT_LOG,
            '0', ModBlocks.AUTO_CRAFTER,
            ' ', Matcher.any());
    public static final IMultiblock RF_CONVERTER = NaturesAuraAPI.instance().createMultiblock(
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
            ' ', Matcher.any());
}
