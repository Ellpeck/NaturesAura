package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.api.recipes.WeightedOre;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import de.ellpeck.naturesaura.api.recipes.ing.NBTIngredient;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public final class ModRecipes {

    public static void init() {
        // TODO recipes
/*        new TreeRitualRecipe(res("eye"),
                ing(new ItemStack(Blocks.SAPLING)), new ItemStack(ModItems.EYE), 250,
                ing(Items.SPIDER_EYE),
                ing(Items.GOLD_INGOT),
                ing(ModItems.GOLD_LEAF),
                ing(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(res("eye_improved"),
                ing(new ItemStack(Blocks.SAPLING)), new ItemStack(ModItems.EYE_IMPROVED), 500,
                ing(ModItems.EYE),
                ing(ModItems.SKY_INGOT),
                ing(ModItems.SKY_INGOT),
                ing(ModBlocks.END_FLOWER),
                ing(ModItems.GOLD_LEAF),
                ing(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(res("nature_altar"),
                ing(Blocks.SAPLING), new ItemStack(ModBlocks.NATURE_ALTAR), 500,
                ing(Blocks.STONE),
                ing(Blocks.STONE),
                ing(Blocks.STONE),
                ing(ModItems.GOLD_LEAF),
                ing(Items.GOLD_INGOT),
                ing(ModItems.TOKEN_JOY)).register();
        new TreeRitualRecipe(res("ancient_sapling"),
                ing(Blocks.SAPLING), new ItemStack(ModBlocks.ANCIENT_SAPLING), 200,
                ing(Blocks.SAPLING),
                ing(Blocks.YELLOW_FLOWER),
                ing(Blocks.RED_FLOWER),
                ing(Items.WHEAT_SEEDS),
                ing(Items.REEDS),
                ing(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(res("furnace_heater"),
                ing(Blocks.SAPLING), new ItemStack(ModBlocks.FURNACE_HEATER), 600,
                ing(ModBlocks.INFUSED_STONE),
                ing(ModBlocks.INFUSED_STONE),
                ing(ModItems.INFUSED_IRON),
                ing(ModItems.INFUSED_IRON),
                ing(Items.FIRE_CHARGE),
                ing(Items.FLINT),
                ing(Blocks.MAGMA),
                ing(ModItems.TOKEN_FEAR)).register();
        new TreeRitualRecipe(res("conversion_catalyst"),
                ing(new ItemStack(Blocks.SAPLING, 1, 3)), new ItemStack(ModBlocks.CONVERSION_CATALYST), 600,
                ing(ModBlocks.GOLD_BRICK),
                ing(ModBlocks.INFUSED_STONE),
                ing(Items.BREWING_STAND),
                ing(ModItems.SKY_INGOT),
                ing(ModItems.GOLD_LEAF),
                ing(Blocks.GLOWSTONE)).register();
        new TreeRitualRecipe(res("crushing_catalyst"),
                ing(Blocks.SAPLING), new ItemStack(ModBlocks.CRUSHING_CATALYST), 600,
                ing(ModBlocks.GOLD_BRICK),
                ing(ModBlocks.INFUSED_STONE),
                ing(Blocks.PISTON),
                ing(Items.FLINT),
                ing(ModItems.TOKEN_ANGER)).register();
        new TreeRitualRecipe(res("plant_powder"),
                ing(new ItemStack(Blocks.SAPLING)),
                EffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 24), PlantBoostEffect.NAME), 400,
                ing(ModBlocks.GOLD_POWDER),
                ing(ModBlocks.GOLD_POWDER),
                ing(ModItems.SKY_INGOT),
                ing(Items.WHEAT)).register();
        new TreeRitualRecipe(res("cache_powder"),
                ing(new ItemStack(Blocks.SAPLING)),
                EffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 32), CacheRechargeEffect.NAME), 400,
                ing(ModBlocks.GOLD_POWDER),
                ing(ModBlocks.GOLD_POWDER),
                ing(ModItems.SKY_INGOT),
                ing(ModItems.AURA_CACHE)).register();
        new TreeRitualRecipe(res("animal_powder"),
                ing(new ItemStack(Blocks.SAPLING, 1, 3)),
                EffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 8), AnimalEffect.NAME), 400,
                ing(ModBlocks.GOLD_POWDER),
                ing(ModBlocks.GOLD_POWDER),
                ing(ModItems.SKY_INGOT),
                ing(Items.EGG)).register();
        new TreeRitualRecipe(res("ore_spawn_powder"),
                ing(new ItemStack(Blocks.SAPLING)),
                EffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 4), OreSpawnEffect.NAME), 400,
                ing(ModBlocks.GOLD_POWDER),
                ing(ModBlocks.GOLD_POWDER),
                ing(Blocks.DIAMOND_ORE),
                ing(Blocks.REDSTONE_ORE)).register();
        new TreeRitualRecipe(res("token_joy"),
                ing(Blocks.SAPLING), new ItemStack(ModItems.TOKEN_JOY, 2), 200,
                nbtIng(AuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_OVERWORLD)),
                ing(ModItems.GOLD_LEAF),
                ing(new ItemStack(Blocks.RED_FLOWER, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Blocks.YELLOW_FLOWER)),
                ing(Items.APPLE),
                ing(Blocks.TORCH),
                ing(Items.IRON_INGOT)).register();
        new TreeRitualRecipe(res("token_anger"),
                ing(Blocks.SAPLING), new ItemStack(ModItems.TOKEN_ANGER, 2), 200,
                nbtIng(AuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_NETHER)),
                ing(ModItems.GOLD_LEAF),
                ing(Blocks.MAGMA),
                ing(Items.BLAZE_POWDER),
                ing(Items.GUNPOWDER),
                ing(Items.ENDER_PEARL)).register();
        new TreeRitualRecipe(res("token_sorrow"),
                ing(Blocks.SAPLING), new ItemStack(ModItems.TOKEN_SORROW, 2), 200,
                nbtIng(AuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_OVERWORLD)),
                ing(ModItems.GOLD_LEAF),
                ing(Items.GHAST_TEAR),
                ing(Items.BEEF, Items.MUTTON, Items.CHICKEN, Items.PORKCHOP),
                ing(Blocks.GLASS),
                ing(new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE))).register();
        new TreeRitualRecipe(res("token_fear"),
                ing(Blocks.SAPLING), new ItemStack(ModItems.TOKEN_FEAR, 2), 200,
                nbtIng(AuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_NETHER)),
                ing(ModItems.GOLD_LEAF),
                ing(Items.ROTTEN_FLESH),
                ing(Items.FEATHER),
                ing(Items.BONE),
                ing(Blocks.SOUL_SAND)).register();

        new AltarRecipe(res("infused_iron"),
                ing(Items.IRON_INGOT), new ItemStack(ModItems.INFUSED_IRON),
                Ingredient.EMPTY, 15000, 80).register();
        new AltarRecipe(res("infused_iron_block"),
                ing(Blocks.IRON_BLOCK), new ItemStack(ModBlocks.INFUSED_IRON),
                Ingredient.EMPTY, 135000, 700).register();
        new AltarRecipe(res("infused_stone"),
                ing(Blocks.STONE), new ItemStack(ModBlocks.INFUSED_STONE),
                Ingredient.EMPTY, 7500, 40).register();

        Ingredient conversion = ing(ModBlocks.CONVERSION_CATALYST);
        new AltarRecipe(res("breath"), nbtIng(AuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_END)), new ItemStack(Items.DRAGON_BREATH), conversion, 20000, 80).register();
        new AltarRecipe(res("leather"), ing(Items.ROTTEN_FLESH), new ItemStack(Items.LEATHER), conversion, 10000, 50).register();
        new AltarRecipe(res("soul_sand"), ing(Blocks.SAND), new ItemStack(Blocks.SOUL_SAND), conversion, 5000, 100).register();
        new AltarRecipe(res("nether_wart"), ing(Blocks.RED_MUSHROOM), new ItemStack(Items.NETHER_WART), conversion, 30000, 250).register();
        new AltarRecipe(res("prismarine"), ing(Items.QUARTZ), new ItemStack(Items.PRISMARINE_SHARD), conversion, 55000, 200).register();
        new AltarRecipe(res("water"), ing(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), Potions.WATER), conversion, 25000, 200).register();
        new AltarRecipe(res("coal"), ing(new ItemStack(Items.COAL, 1, 1)), new ItemStack(Items.COAL), conversion, 30000, 250).register();

        Ingredient crushing = ing(ModBlocks.CRUSHING_CATALYST);
        new AltarRecipe(res("bone"), ing(Items.BONE), new ItemStack(Items.DYE, 6, 15), crushing, 3000, 40).register();
        new AltarRecipe(res("sugar"), ing(Items.REEDS), new ItemStack(Items.SUGAR, 3), crushing, 3000, 40).register();
        new AltarRecipe(res("blaze"), ing(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4), crushing, 5000, 60).register();
        new AltarRecipe(res("glowstone"), ing(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4), crushing, 3000, 40).register();
        new AltarRecipe(res("sand"), ing(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND), crushing, 3000, 40).register();

        new OfferingRecipe(res("sky_ingot"),
                amountIng(new ItemStack(ModItems.INFUSED_IRON, 3)),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.SKY_INGOT)).register();
        new OfferingRecipe(res("clock_hand"),
                ing(Items.NETHER_STAR),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.CLOCK_HAND)).register();
        new OfferingRecipe(res("token_euphoria"),
                ing(ModItems.TOKEN_JOY),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.TOKEN_EUPHORIA)).register();
        new OfferingRecipe(res("token_rage"),
                ing(ModItems.TOKEN_ANGER),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.TOKEN_RAGE)).register();
        new OfferingRecipe(res("token_grief"),
                ing(ModItems.TOKEN_SORROW),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.TOKEN_GRIEF)).register();
        new OfferingRecipe(res("token_terror"),
                ing(ModItems.TOKEN_FEAR),
                ing(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.TOKEN_TERROR)).register();

        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE.getDefaultState(),
                Blocks.MOSSY_COBBLESTONE.getDefaultState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT),
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY));
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE_WALL.getDefaultState().withProperty(WallBlock.VARIANT, WallBlock.EnumType.NORMAL),
                Blocks.COBBLESTONE_WALL.getDefaultState().withProperty(WallBlock.VARIANT, WallBlock.EnumType.MOSSY));

        for (Block block : ForgeRegistries.BLOCKS)
            if (block instanceof FlowerBlock)
                NaturesAuraAPI.FLOWERS.addAll(block.getBlockState().getValidStates());

        spawner("cow", "minecraft:cow", 50000, 60, ing(Items.BEEF), ing(Items.LEATHER));
        for (DyeColor color : DyeColor.values())
            new AnimalSpawnerRecipe(res("sheep_" + color.getName()), new ResourceLocation("minecraft:sheep"),
                    500, 60, ing(ModItems.BIRTH_SPIRIT), ing(Items.MUTTON),
                    ing(new ItemStack(Blocks.WOOL, 1, color.getMetadata()))) {
                @Override
                public Entity makeEntity(World world, double x, double y, double z) {
                    SheepEntity sheep = (SheepEntity) super.makeEntity(world, x, y, z);
                    sheep.setFleeceColor(color);
                    return sheep;
                }
            }.register();
        spawner("chicken", "minecraft:chicken", 30000, 40, ing(Items.FEATHER), ing(Items.EGG));
        spawner("pig", "minecraft:pig", 50000, 60, ing(Items.PORKCHOP));
        spawner("blaze", "minecraft:blaze", 150000, 120, ing(Items.BLAZE_ROD), ing(Items.BLAZE_POWDER));
        spawner("ghast", "minecraft:ghast", 120000, 150, ing(Items.GUNPOWDER), ing(Items.GHAST_TEAR));
        spawner("ocelot", "minecraft:ocelot", 80000, 60, ing(Items.FISH), ing(Blocks.WOOL));
        spawner("mule", "minecraft:mule", 100000, 100, ing(Items.LEATHER), ing(Blocks.CHEST), ing(Items.APPLE));
        spawner("bat", "minecraft:bat", 30000, 40, ing(Items.FEATHER));
        spawner("endermite", "minecraft:endermite", 30000, 40, ing(Items.ENDER_PEARL), ing(Blocks.STONE));
        spawner("parrot", "minecraft:parrot", 50000, 60, ing(Items.FEATHER), ing(Items.COOKIE));
        spawner("slime", "minecraft:slime", 30000, 40, ing(Items.SLIME_BALL));
        spawner("spider", "minecraft:spider", 100000, 120, ing(Items.STRING), ing(Items.SPIDER_EYE));
        spawner("skeleton", "minecraft:skeleton", 100000, 120, ing(Items.BONE), ing(Items.ARROW));
        spawner("enderman", "minecraft:enderman", 120000, 120, ing(Items.ENDER_PEARL));
        spawner("silverfish", "minecraft:silverfish", 30000, 40, ing(Blocks.STONE));
        spawner("squid", "minecraft:squid", 50000, 40, ing(new ItemStack(Items.DYE, 1, DyeColor.BLACK.getDyeDamage())));
        spawner("stray", "minecraft:stray", 100000, 120, ing(Items.BONE), ing(Blocks.ICE));
        spawner("shulker", "minecraft:shulker", 150000, 100, ing(Items.SHULKER_SHELL));
        spawner("husk", "minecraft:husk", 100000, 120, ing(Items.ROTTEN_FLESH), ing(Blocks.SAND));
        spawner("llama", "minecraft:llama", 60000, 80, ing(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE)));
        spawner("rabbit", "minecraft:rabbit", 30000, 40, ing(Items.RABBIT_HIDE));
        spawner("magma_cube", "minecraft:magma_cube", 100000, 100, ing(Items.MAGMA_CREAM));
        spawner("zombie_pigman", "minecraft:zombie_pigman", 120000, 150, ing(Items.ROTTEN_FLESH), ing(Items.GOLD_NUGGET));
        spawner("polar_bear", "minecraft:polar_bear", 50000, 60, ing(Items.FISH), ing(Blocks.ICE));
        spawner("mooshroom", "minecraft:mooshroom", 40000, 60, ing(Items.LEATHER), ing(Blocks.RED_MUSHROOM));
        spawner("guardian", "minecraft:guardian", 150000, 150, ing(Items.PRISMARINE_SHARD), ing(Items.PRISMARINE_CRYSTALS));
        spawner("horse", "minecraft:horse", 100000, 100, ing(Items.LEATHER));
        spawner("donkey", "minecraft:donkey", 100000, 100, ing(Items.LEATHER), ing(Blocks.CHEST));
        spawner("cave_spider", "minecraft:cave_spider", 100000, 120, ing(Items.STRING), ing(Items.FERMENTED_SPIDER_EYE));
        spawner("creeper", "minecraft:creeper", 100000, 120, ing(Items.GUNPOWDER));
        spawner("witch", "minecraft:witch", 150000, 150, ing(Items.GLASS_BOTTLE), ing(Items.GLOWSTONE_DUST));
        spawner("wither_skeleton", "minecraft:wither_skeleton", 150000, 150, ing(Items.BONE), ing(Blocks.OBSIDIAN));
        spawner("wolf", "minecraft:wolf", 50000, 60, ing(Items.LEATHER), ing(Items.BONE));
        spawner("zombie", "minecraft:zombie", 100000, 100, ing(Items.ROTTEN_FLESH));*/

        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreCoal", 5000));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherCoal", 5000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreIron", 3000));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherIron", 3000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreGold", 500));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherGold", 500));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreDiamond", 50));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherDiamond", 50));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreLapis", 250));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherLapis", 250));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreRedstone", 200));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherRedstone", 200));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreEmerald", 30));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreQuartz", 3000));

        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreCopper", 2000));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherCopper", 2000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreTin", 1800));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherTin", 1800));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreLead", 1500));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherLead", 1500));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreSilver", 1000));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherSilver", 1000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreNickel", 100));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherNickel", 100));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePlatinum", 20));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreNetherPlatinum", 20));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreAluminum", 1200));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreAluminium", 1200));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreOsmium", 1500));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreZinc", 1000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreYellorite", 1200));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreUranium", 400));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreCertusQuartz", 800));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreApatite", 700));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreQuartzBlack", 3000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreRuby", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePeridot", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreTopaz", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreTanzanite", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreMalachite", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreSapphire", 40));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreAmber", 150));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreResonating", 50));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreSulfur", 3000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreSaltpeter", 250));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreFirestone", 30));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreSalt", 2900));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("oreDraconium", 5));

        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorIron", 3000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorGold", 500));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorCopper", 2000));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorTin", 1800));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorLead", 1500));
        NaturesAuraAPI.OVERWORLD_ORES.add(new WeightedOre("orePoorSilver", 1000));

        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreCobalt", 50));
        NaturesAuraAPI.NETHER_ORES.add(new WeightedOre("oreArdite", 50));

        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("egg"), 2500);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("snowball"), 3500);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("small_fireball"), 15000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("ender_pearl"), 30000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("xp_bottle"), 75000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("arrow"), 10000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("shulker_bullet"), 250000);
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(new ResourceLocation("llama_spit"), 80000);
    }

    private static void spawner(String name, String entity, int aura, int time, Ingredient... ings) {
        Ingredient[] actualIngs = new Ingredient[ings.length + 1];
        actualIngs[0] = ing(ModItems.BIRTH_SPIRIT);
        System.arraycopy(ings, 0, actualIngs, 1, ings.length);
        new AnimalSpawnerRecipe(res(name), new ResourceLocation(entity), aura, time, actualIngs).register();
    }

    private static Ingredient ing(Block... blocks) {
        return ing(Arrays.stream(blocks).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    private static Ingredient ing(Item... items) {
        return ing(Arrays.stream(items).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    private static Ingredient ing(ItemStack... stacks) {
        return Ingredient.fromStacks(stacks);
    }

    private static Ingredient nbtIng(ItemStack stack) {
        return new NBTIngredient(stack);
    }

    private static Ingredient amountIng(ItemStack stack) {
        return new AmountIngredient(stack);
    }

    private static ResourceLocation res(String name) {
        return new ResourceLocation(NaturesAura.MOD_ID, name);
    }
}
