package de.ellpeck.naturesaura.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

@SuppressWarnings("FieldNamingConvention")
public final class ModBlocks {

    public static Block ANCIENT_LOG;
    public static Block ANCIENT_BARK;
    public static Block ANCIENT_PLANKS;
    public static Block ANCIENT_STAIRS;
    public static Block ANCIENT_SLAB;
    public static Block ANCIENT_LEAVES;
    public static Block ANCIENT_SAPLING;
    public static Block NATURE_ALTAR;
    public static Block DECAYED_LEAVES;
    public static Block GOLDEN_LEAVES;
    public static Block GOLD_POWDER;
    public static Block WOOD_STAND;
    public static Block INFUSED_STONE;
    public static Block INFUSED_STAIRS;
    public static Block INFUSED_SLAB;
    public static Block INFUSED_BRICK;
    public static Block INFUSED_BRICK_STAIRS;
    public static Block INFUSED_BRICK_SLAB;
    public static Block FURNACE_HEATER;
    public static Block POTION_GENERATOR;
    public static Block AURA_DETECTOR;
    public static Block CONVERSION_CATALYST;
    public static Block CRUSHING_CATALYST;
    public static Block FLOWER_GENERATOR;
    public static Block PLACER;
    public static Block HOPPER_UPGRADE;
    public static Block FIELD_CREATOR;
    public static Block OAK_GENERATOR;
    public static Block INFUSED_IRON_BLOCK;
    public static Block OFFERING_TABLE;
    public static Block PICKUP_STOPPER;
    public static Block SPAWN_LAMP;
    public static Block ANIMAL_GENERATOR;
    public static Block END_FLOWER;
    public static Block GRATED_CHUTE;
    public static Block ANIMAL_SPAWNER;
    public static Block AUTO_CRAFTER;
    public static Block GOLD_BRICK;
    public static Block GOLD_NETHER_BRICK;
    public static Block RF_CONVERTER;
    public static Block MOSS_GENERATOR;
    public static Block TIME_CHANGER;
    public static Block GENERATOR_LIMIT_REMOVER;
    public static Block ENDER_CRATE;
    public static Block POWDER_PLACER;
    public static Block FIREWORK_GENERATOR;
    public static Block PROJECTILE_GENERATOR;
    public static Block CHUNK_LOADER;
    public static Block DIMENSION_RAIL_OVERWORLD;
    public static Block DIMENSION_RAIL_NETHER;
    public static Block DIMENSION_RAIL_END;
    public static Block BLAST_FURNACE_BOOSTER;
    public static Block NETHER_WART_MUSHROOM;
    public static Block ANIMAL_CONTAINER;
    public static Block SNOW_CREATOR;
    public static Block ITEM_DISTRIBUTOR;
    public static Block AURA_BLOOM;
    public static Block AURA_CACTUS;
    public static Block TAINTED_GOLD_BLOCK;
    public static Block NETHER_GRASS;
    public static Block LIGHT;
    public static Block CHORUS_GENERATOR;
    public static Block AURA_TIMER;

    public static Block.Properties prop(Material material, MaterialColor color) {
        return Block.Properties.create(material, color);
    }

    public static Block.Properties prop(Material material) {
        return Block.Properties.create(material);
    }

    public static Block.Properties prop(Block block) {
        return Block.Properties.from(block);
    }

}
