package de.ellpeck.naturesaura.recipes;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import de.ellpeck.naturesaura.api.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.api.recipes.ing.AmountIngredient;
import de.ellpeck.naturesaura.api.recipes.ing.NBTIngredient;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.chunk.effect.AnimalEffect;
import de.ellpeck.naturesaura.chunk.effect.CacheRechargeEffect;
import de.ellpeck.naturesaura.chunk.effect.PlantBoostEffect;
import de.ellpeck.naturesaura.items.ItemAuraBottle;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ModRecipes {

    public static void init() {
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "eye"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING)), new ItemStack(ModItems.EYE), 250,
                Ingredient.fromItem(Items.SPIDER_EYE),
                Ingredient.fromItem(Items.GOLD_INGOT),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Ingredient.fromItem(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "eye_improved"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING)), new ItemStack(ModItems.EYE_IMPROVED), 500,
                Ingredient.fromItem(ModItems.EYE),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Helper.blockIng(ModBlocks.END_FLOWER),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Ingredient.fromItem(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "nature_altar"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.NATURE_ALTAR), 500,
                Helper.blockIng(Blocks.STONE),
                Helper.blockIng(Blocks.STONE),
                Helper.blockIng(Blocks.STONE),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Ingredient.fromItem(Items.GOLD_INGOT),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_OVERWORLD))).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "ancient_sapling"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.ANCIENT_SAPLING), 200,
                Helper.blockIng(Blocks.SAPLING),
                Helper.blockIng(Blocks.YELLOW_FLOWER),
                Helper.blockIng(Blocks.RED_FLOWER),
                Ingredient.fromItem(Items.WHEAT_SEEDS),
                Ingredient.fromItem(Items.REEDS),
                Ingredient.fromItem(ModItems.GOLD_LEAF)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "furnace_heater"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.FURNACE_HEATER), 600,
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Ingredient.fromItem(ModItems.INFUSED_IRON),
                Ingredient.fromItem(ModItems.INFUSED_IRON),
                Ingredient.fromItem(Items.FIRE_CHARGE),
                Ingredient.fromItem(Items.FLINT),
                Helper.blockIng(Blocks.MAGMA),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_NETHER))).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "conversion_catalyst"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 3)), new ItemStack(ModBlocks.CONVERSION_CATALYST), 600,
                Helper.blockIng(ModBlocks.GOLD_BRICK),
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Ingredient.fromItem(Items.BREWING_STAND),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(ModItems.GOLD_LEAF),
                Helper.blockIng(Blocks.GLOWSTONE)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "crushing_catalyst"),
                Helper.blockIng(Blocks.SAPLING), new ItemStack(ModBlocks.CRUSHING_CATALYST), 600,
                Helper.blockIng(ModBlocks.GOLD_BRICK),
                Helper.blockIng(ModBlocks.INFUSED_STONE),
                Helper.blockIng(Blocks.PISTON),
                Ingredient.fromItem(Items.FLINT)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "plant_powder"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING)),
                ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 24), PlantBoostEffect.NAME), 400,
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(Items.WHEAT)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "cache_powder"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING)),
                ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 32), CacheRechargeEffect.NAME), 400,
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(ModItems.AURA_CACHE)).register();
        new TreeRitualRecipe(new ResourceLocation(NaturesAura.MOD_ID, "animal_powder"),
                Ingredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 3)),
                ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, 24), AnimalEffect.NAME), 400,
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Helper.blockIng(ModBlocks.GOLD_POWDER),
                Ingredient.fromItem(ModItems.SKY_INGOT),
                Ingredient.fromItem(Items.EGG)).register();

        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_iron"),
                Ingredient.fromItem(Items.IRON_INGOT), new ItemStack(ModItems.INFUSED_IRON),
                Ingredient.EMPTY, 15000, 80).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_iron_block"),
                Helper.blockIng(Blocks.IRON_BLOCK), new ItemStack(ModBlocks.INFUSED_IRON),
                Ingredient.EMPTY, 135000, 700).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "infused_stone"),
                Helper.blockIng(Blocks.STONE), new ItemStack(ModBlocks.INFUSED_STONE),
                Ingredient.EMPTY, 7500, 40).register();

        Ingredient conversion = Helper.blockIng(ModBlocks.CONVERSION_CATALYST);
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "breath"),
                new NBTIngredient(ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), NaturesAuraAPI.TYPE_END)),
                new ItemStack(Items.DRAGON_BREATH),
                conversion, 20000, 80).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "leather"),
                Ingredient.fromItem(Items.ROTTEN_FLESH), new ItemStack(Items.LEATHER),
                conversion, 10000, 50).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "soul_sand"),
                Helper.blockIng(Blocks.SAND), new ItemStack(Blocks.SOUL_SAND),
                conversion, 5000, 100).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "nether_wart"),
                Helper.blockIng(Blocks.RED_MUSHROOM), new ItemStack(Items.NETHER_WART),
                conversion, 30000, 250).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "prismarine"),
                Ingredient.fromItem(Items.QUARTZ), new ItemStack(Items.PRISMARINE_SHARD),
                conversion, 55000, 200).register();

        Ingredient crushing = Helper.blockIng(ModBlocks.CRUSHING_CATALYST);
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "bone"),
                Ingredient.fromItem(Items.BONE), new ItemStack(Items.DYE, 6, 15),
                crushing, 3000, 40).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "sugar"),
                Ingredient.fromItem(Items.REEDS), new ItemStack(Items.SUGAR, 3),
                crushing, 3000, 40).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "blaze"),
                Ingredient.fromItem(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 4),
                crushing, 5000, 60).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "glowstone"),
                Helper.blockIng(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4),
                crushing, 3000, 40).register();
        new AltarRecipe(new ResourceLocation(NaturesAura.MOD_ID, "sand"),
                Helper.blockIng(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND),
                crushing, 3000, 40).register();

        new OfferingRecipe(new ResourceLocation(NaturesAura.MOD_ID, "sky_ingot"),
                new AmountIngredient(new ItemStack(ModItems.INFUSED_IRON, 3)),
                Ingredient.fromItem(ModItems.CALLING_SPIRIT),
                new ItemStack(ModItems.SKY_INGOT)).register();

        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.COBBLESTONE.getDefaultState(),
                Blocks.MOSSY_COBBLESTONE.getDefaultState());
        NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.put(
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT),
                Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY));

        for (Block block : ForgeRegistries.BLOCKS)
            if (block instanceof BlockFlower)
                NaturesAuraAPI.FLOWERS.addAll(block.getBlockState().getValidStates());

        spawner("cow", "minecraft:cow", 50000, 60, Ingredient.fromItem(Items.BEEF), Ingredient.fromItem(Items.LEATHER));
        for (EnumDyeColor color : EnumDyeColor.values())
            new AnimalSpawnerRecipe(new ResourceLocation(NaturesAura.MOD_ID, "sheep_" + color.getName()), new ResourceLocation("minecraft:sheep"),
                    500, 60, Ingredient.fromItem(ModItems.BIRTH_SPIRIT), Ingredient.fromItem(Items.MUTTON),
                    Ingredient.fromStacks(new ItemStack(Blocks.WOOL, 1, color.getMetadata()))) {
                @Override
                public Entity makeEntity(World world, double x, double y, double z) {
                    EntitySheep sheep = (EntitySheep) super.makeEntity(world, x, y, z);
                    sheep.setFleeceColor(color);
                    return sheep;
                }
            }.register();
        spawner("chicken", "minecraft:chicken", 30000, 40, Ingredient.fromItem(Items.FEATHER), Ingredient.fromItem(Items.EGG));
        spawner("pig", "minecraft:pig", 50000, 60, Ingredient.fromItem(Items.PORKCHOP));
        spawner("blaze", "minecraft:blaze", 150000, 120, Ingredient.fromItem(Items.BLAZE_ROD), Ingredient.fromItem(Items.BLAZE_POWDER));
        spawner("ghast", "minecraft:ghast", 120000, 150, Ingredient.fromItem(Items.GUNPOWDER), Ingredient.fromItem(Items.GHAST_TEAR));
        spawner("ocelot", "minecraft:ocelot", 80000, 60, Ingredient.fromItem(Items.FISH), Helper.blockIng(Blocks.WOOL));
        spawner("mule", "minecraft:mule", 100000, 100, Ingredient.fromItem(Items.LEATHER), Helper.blockIng(Blocks.CHEST), Ingredient.fromItem(Items.APPLE));
        spawner("bat", "minecraft:bat", 30000, 40, Ingredient.fromItem(Items.FEATHER));
        spawner("endermite", "minecraft:endermite", 30000, 40, Ingredient.fromItem(Items.ENDER_PEARL), Helper.blockIng(Blocks.STONE));
        spawner("parrot", "minecraft:parrot", 50000, 60, Ingredient.fromItem(Items.FEATHER), Ingredient.fromItem(Items.COOKIE));
        spawner("slime", "minecraft:slime", 30000, 40, Ingredient.fromItem(Items.SLIME_BALL));
        spawner("spider", "minecraft:spider", 100000, 120, Ingredient.fromItem(Items.STRING), Ingredient.fromItem(Items.SPIDER_EYE));
        spawner("skeleton", "minecraft:skeleton", 100000, 120, Ingredient.fromItem(Items.BONE), Ingredient.fromItem(Items.ARROW));
        spawner("enderman", "minecraft:enderman", 120000, 120, Ingredient.fromItem(Items.ENDER_PEARL));
        spawner("silverfish", "minecraft:silverfish", 30000, 40, Helper.blockIng(Blocks.STONE));
        spawner("squid", "minecraft:squid", 50000, 40, Ingredient.fromStacks(new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage())));
        spawner("stray", "minecraft:stray", 100000, 120, Ingredient.fromItem(Items.BONE), Helper.blockIng(Blocks.ICE));
        spawner("shulker", "minecraft:shulker", 150000, 100, Ingredient.fromItem(Items.SHULKER_SHELL));
        spawner("husk", "minecraft:husk", 100000, 120, Ingredient.fromItem(Items.ROTTEN_FLESH), Helper.blockIng(Blocks.SAND));
        spawner("llama", "minecraft:llama", 60000, 80, Ingredient.fromStacks(new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE)));
        spawner("rabbit", "minecraft:rabbit", 30000, 40, Ingredient.fromItem(Items.RABBIT_HIDE));
        spawner("magma_cube", "minecraft:magma_cube", 100000, 100, Ingredient.fromItem(Items.MAGMA_CREAM));
        spawner("zombie_pigman", "minecraft:zombie_pigman", 120000, 150, Ingredient.fromItem(Items.ROTTEN_FLESH), Ingredient.fromItem(Items.GOLD_NUGGET));
        spawner("polar_bear", "minecraft:polar_bear", 50000, 60, Ingredient.fromItem(Items.FISH), Helper.blockIng(Blocks.ICE));
        spawner("mooshroom", "minecraft:mooshroom", 40000, 60, Ingredient.fromItem(Items.LEATHER), Helper.blockIng(Blocks.RED_MUSHROOM));
        spawner("guardian", "minecraft:guardian", 150000, 150, Ingredient.fromItem(Items.PRISMARINE_SHARD), Ingredient.fromItem(Items.PRISMARINE_CRYSTALS));
        spawner("horse", "minecraft:horse", 100000, 100, Ingredient.fromItem(Items.LEATHER));
        spawner("donkey", "minecraft:donkey", 100000, 100, Ingredient.fromItem(Items.LEATHER), Helper.blockIng(Blocks.CHEST));
        spawner("cave_spider", "minecraft:cave_spider", 100000, 120, Ingredient.fromItem(Items.STRING), Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE));
        spawner("creeper", "minecraft:creeper", 100000, 120, Ingredient.fromItem(Items.GUNPOWDER));
        spawner("witch", "minecraft:witch", 150000, 150, Ingredient.fromItem(Items.GLASS_BOTTLE), Ingredient.fromItem(Items.GLOWSTONE_DUST));
        spawner("wither_skeleton", "minecraft:wither_skeleton", 150000, 150, Ingredient.fromItem(Items.BONE), Helper.blockIng(Blocks.OBSIDIAN));
        spawner("wolf", "minecraft:wolf", 50000, 60, Ingredient.fromItem(Items.LEATHER), Ingredient.fromItem(Items.BONE));
        spawner("zombie", "minecraft:zombie", 100000, 100, Ingredient.fromItem(Items.ROTTEN_FLESH));
    }

    private static void spawner(String name, String entity, int aura, int time, Ingredient... ings) {
        Ingredient[] actualIngs = new Ingredient[ings.length + 1];
        actualIngs[0] = Ingredient.fromItem(ModItems.BIRTH_SPIRIT);
        System.arraycopy(ings, 0, actualIngs, 1, ings.length);
        new AnimalSpawnerRecipe(new ResourceLocation(NaturesAura.MOD_ID, name), new ResourceLocation(entity), aura, time, actualIngs).register();
    }
}
