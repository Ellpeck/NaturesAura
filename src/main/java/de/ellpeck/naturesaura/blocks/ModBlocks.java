package de.ellpeck.naturesaura.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public final class ModBlocks {

    public static final Block ANCIENT_LOG = new BlockAncientLog();
    public static final Block ANCIENT_BARK = new BlockImpl("ancient_bark", Material.WOOD).setSoundType(SoundType.WOOD).setHardness(2F);
    public static final Block ANCIENT_PLANKS = new BlockImpl("ancient_planks", Material.WOOD).setSoundType(SoundType.WOOD).setHardness(2F);
    public static final Block ANCIENT_STAIRS = new BlockStairsNA("ancient_stairs", ANCIENT_PLANKS.getDefaultState());
    public static final Block ANCIENT_SLAB = BlockSlabsNA.makeSlab("ancient_slab", Material.WOOD, SoundType.WOOD, 1.5F);
    public static final Block ANCIENT_LEAVES = new BlockAncientLeaves();
    public static final Block ANCIENT_SAPLING = new BlockAncientSapling();
    public static final Block NATURE_ALTAR = new BlockNatureAltar();
    public static final Block DECAYED_LEAVES = new BlockDecayedLeaves();
    public static final Block GOLDEN_LEAVES = new BlockGoldenLeaves();
    public static final Block GOLD_POWDER = new BlockGoldPowder();
    public static final Block WOOD_STAND = new BlockWoodStand();
    public static final Block INFUSED_STONE = new BlockImpl("infused_stone", Material.ROCK).setSoundType(SoundType.STONE).setHardness(1.75F);
    public static final Block INFUSED_STAIRS = new BlockStairsNA("infused_stairs", INFUSED_STONE.getDefaultState());
    public static final Block INFUSED_SLAB = BlockSlabsNA.makeSlab("infused_slab", Material.ROCK, SoundType.STONE, 1.25F);
    public static final Block INFUSED_BRICK = new BlockImpl("infused_brick", Material.ROCK).setSoundType(SoundType.STONE).setHardness(1.5F);
    public static final Block INFUSED_BRICK_STAIRS = new BlockStairsNA("infused_brick_stairs", INFUSED_BRICK.getDefaultState());
    public static final Block INFUSED_BRICK_SLAB = BlockSlabsNA.makeSlab("infused_brick_slab", Material.ROCK, SoundType.STONE, 1.25F);
    public static final Block FURNACE_HEATER = new BlockFurnaceHeater();
    public static final Block POTION_GENERATOR = new BlockPotionGenerator();
    public static final Block AURA_DETECTOR = new BlockAuraDetector();
    public static final Block CONVERSION_CATALYST = new BlockImpl("conversion_catalyst", Material.ROCK).setSoundType(SoundType.STONE).setHardness(2.5F);
    public static final Block FLOWER_GENERATOR = new BlockFlowerGenerator();
    public static final Block PLACER = new BlockPlacer();
}
