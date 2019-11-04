package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_log")
    public static Block ANCIENT_LOG;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_bark")
    public static Block ANCIENT_BARK;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_planks")
    public static Block ANCIENT_PLANKS;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_stairs")
    public static Block ANCIENT_STAIRS;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_slab")
    public static Block ANCIENT_SLAB;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_leaves")
    public static Block ANCIENT_LEAVES;
    @ObjectHolder(NaturesAura.MOD_ID + ":ancient_sapling")
    public static Block ANCIENT_SAPLING;
    @ObjectHolder(NaturesAura.MOD_ID + ":nature_altar")
    public static Block NATURE_ALTAR;
    @ObjectHolder(NaturesAura.MOD_ID + ":decayed_leaves")
    public static Block DECAYED_LEAVES;
    @ObjectHolder(NaturesAura.MOD_ID + ":golden_leaves")
    public static Block GOLDEN_LEAVES;
    @ObjectHolder(NaturesAura.MOD_ID + ":gold_powder")
    public static Block GOLD_POWDER;
    @ObjectHolder(NaturesAura.MOD_ID + ":wood_stand")
    public static Block WOOD_STAND;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_stone")
    public static Block INFUSED_STONE;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_stairs")
    public static Block INFUSED_STAIRS;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_slab")
    public static Block INFUSED_SLAB;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_brick")
    public static Block INFUSED_BRICK;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_brick_stairs")
    public static Block INFUSED_BRICK_STAIRS;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_brick_slab")
    public static Block INFUSED_BRICK_SLAB;
    @ObjectHolder(NaturesAura.MOD_ID + ":furnace_heater")
    public static Block FURNACE_HEATER;
    @ObjectHolder(NaturesAura.MOD_ID + ":potion_generator")
    public static Block POTION_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":aura_detector")
    public static Block AURA_DETECTOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":conversion_catalyst")
    public static Block CONVERSION_CATALYST;
    @ObjectHolder(NaturesAura.MOD_ID + ":crushing_catalyst")
    public static Block CRUSHING_CATALYST;
    @ObjectHolder(NaturesAura.MOD_ID + ":flower_generator")
    public static Block FLOWER_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":placer")
    public static Block PLACER;
    @ObjectHolder(NaturesAura.MOD_ID + ":hopper_upgrade")
    public static Block HOPPER_UPGRADE;
    @ObjectHolder(NaturesAura.MOD_ID + ":field_creator")
    public static Block FIELD_CREATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":oak_generator")
    public static Block OAK_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":infused_iron_block")
    public static Block INFUSED_IRON;
    @ObjectHolder(NaturesAura.MOD_ID + ":offering_table")
    public static Block OFFERING_TABLE;
    @ObjectHolder(NaturesAura.MOD_ID + ":pickup_stopper")
    public static Block PICKUP_STOPPER;
    @ObjectHolder(NaturesAura.MOD_ID + ":spawn_lamp")
    public static Block SPAWN_LAMP;
    @ObjectHolder(NaturesAura.MOD_ID + ":animal_generator")
    public static Block ANIMAL_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":end_flower")
    public static Block END_FLOWER;
    @ObjectHolder(NaturesAura.MOD_ID + ":grated_chute")
    public static Block GRATED_CHUTE;
    @ObjectHolder(NaturesAura.MOD_ID + ":animal_spawner")
    public static Block ANIMAL_SPAWNER;
    @ObjectHolder(NaturesAura.MOD_ID + ":auto_crafter")
    public static Block AUTO_CRAFTER;
    @ObjectHolder(NaturesAura.MOD_ID + ":gold_brick")
    public static Block GOLD_BRICK;
    @ObjectHolder(NaturesAura.MOD_ID + ":rf_converter")
    public static Block RF_CONVERTER;
    @ObjectHolder(NaturesAura.MOD_ID + ":moss_generator")
    public static Block MOSS_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":time_changer")
    public static Block TIME_CHANGER;
    @ObjectHolder(NaturesAura.MOD_ID + ":generator_limit_remover")
    public static Block GENERATOR_LIMIT_REMOVER;
    @ObjectHolder(NaturesAura.MOD_ID + ":ender_crate")
    public static Block ENDER_CRATE;
    @ObjectHolder(NaturesAura.MOD_ID + ":powder_placer")
    public static Block POWDER_PLACER;
    @ObjectHolder(NaturesAura.MOD_ID + ":firework_generator")
    public static Block FIREWORK_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":projectile_generator")
    public static Block PROJECTILE_GENERATOR;
    @ObjectHolder(NaturesAura.MOD_ID + ":chunk_loader")
    public static Block CHUNK_LOADER;
    @ObjectHolder(NaturesAura.MOD_ID + ":dimentional_rail_overworld")
    public static Block DIMENSION_RAIL_OVERWORLD;
    @ObjectHolder(NaturesAura.MOD_ID + ":dimentional_rail_nether")
    public static Block DIMENSION_RAIL_NETHER;
    @ObjectHolder(NaturesAura.MOD_ID + ":dimentional_rail_end")
    public static Block DIMENSION_RAIL_END;

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
