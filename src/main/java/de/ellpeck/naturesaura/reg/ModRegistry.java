package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.*;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraBloom;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityEnderCrate;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.enchant.AuraMendingEnchantment;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import de.ellpeck.naturesaura.entities.*;
import de.ellpeck.naturesaura.gen.LevelGenAncientTree;
import de.ellpeck.naturesaura.gen.LevelGenAuraBloom;
import de.ellpeck.naturesaura.gen.LevelGenNetherWartMushroom;
import de.ellpeck.naturesaura.gen.ModFeatures;
import de.ellpeck.naturesaura.gui.ContainerEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import de.ellpeck.naturesaura.items.*;
import de.ellpeck.naturesaura.items.tools.*;
import de.ellpeck.naturesaura.potion.ModPotions;
import de.ellpeck.naturesaura.potion.PotionBreathless;
import de.ellpeck.naturesaura.recipes.EnabledCondition;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModRegistry {

    public static final Set<IModItem> ALL_ITEMS = new HashSet<>();

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS, h -> {
            Block temp;
            ModRegistry.registerAll(h,
                    new BlockAncientLog("ancient_log"),
                    new BlockAncientLog("ancient_bark"),
                    temp = new BlockImpl("ancient_planks", Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2F)),
                    new BlockStairsNA("ancient_stairs", "ancient_planks", temp::defaultBlockState, Block.Properties.copy(temp)),
                    new Slab("ancient_slab", "ancient_planks", Block.Properties.copy(temp)),
                    new BlockAncientLeaves(),
                    new BlockAncientSapling(),
                    new BlockNatureAltar(),
                    new BlockDecayedLeaves(),
                    new BlockGoldenLeaves(),
                    new BlockGoldPowder(),
                    new BlockWoodStand(),
                    temp = new BlockImpl("infused_stone", Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.75F)),
                    new BlockStairsNA("infused_stairs", "infused_stone", temp::defaultBlockState, Block.Properties.copy(temp)),
                    new Slab("infused_slab", "infused_stone", Block.Properties.copy(temp)),
                    temp = new BlockImpl("infused_brick", Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5F)),
                    new BlockStairsNA("infused_brick_stairs", "infused_brick", temp::defaultBlockState, Block.Properties.copy(temp)),
                    new Slab("infused_brick_slab", "infused_brick", Block.Properties.copy(temp)),
                    new BlockFurnaceHeater(),
                    new BlockPotionGenerator(),
                    new BlockAuraDetector(),
                    new BlockCatalyst("conversion_catalyst", Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.5F)),
                    new BlockCatalyst("crushing_catalyst", Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.5F)),
                    new BlockFlowerGenerator(),
                    new BlockPlacer(),
                    new BlockHopperUpgrade(),
                    new BlockFieldCreator(),
                    new BlockOakGenerator(),
                    new BlockImpl("infused_iron_block", Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3F)),
                    new BlockOfferingTable(),
                    new BlockPickupStopper(),
                    new BlockSpawnLamp(),
                    new BlockAnimalGenerator(),
                    new BlockEndFlower(),
                    new BlockGratedChute(),
                    new BlockAnimalSpawner(),
                    new BlockAutoCrafter(),
                    new BlockImpl("gold_brick", Block.Properties.copy(Blocks.STONE_BRICKS)),
                    new BlockImpl("gold_nether_brick", Block.Properties.copy(Blocks.NETHER_BRICKS)),
                    new BlockMossGenerator(),
                    new BlockTimeChanger(),
                    new BlockGeneratorLimitRemover(),
                    new BlockEnderCrate(),
                    new BlockPowderPlacer(),
                    new BlockFireworkGenerator(),
                    new BlockProjectileGenerator(),
                    new BlockDimensionRail("overworld", Level.OVERWORLD, Level.NETHER, Level.END),
                    new BlockDimensionRail("nether", Level.NETHER, Level.OVERWORLD),
                    new BlockDimensionRail("end", Level.END, Level.OVERWORLD),
                    new BlockBlastFurnaceBooster(),
                    new BlockImpl("nether_wart_mushroom", Block.Properties.copy(Blocks.RED_MUSHROOM_BLOCK)),
                    new BlockAnimalContainer(),
                    new BlockSnowCreator(),
                    new BlockItemDistributor(),
                    temp = new BlockAuraBloom("aura_bloom", Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.FARMLAND),
                    ModRegistry.createFlowerPot(temp),
                    temp = new BlockAuraBloom("aura_cactus", Blocks.SAND, Blocks.RED_SAND),
                    ModRegistry.createFlowerPot(temp),
                    temp = new BlockAuraBloom("warped_aura_mushroom", Blocks.WARPED_NYLIUM),
                    ModRegistry.createFlowerPot(temp),
                    temp = new BlockAuraBloom("crimson_aura_mushroom", Blocks.CRIMSON_NYLIUM),
                    ModRegistry.createFlowerPot(temp),
                    temp = new BlockAuraBloom("aura_mushroom", Blocks.MYCELIUM),
                    ModRegistry.createFlowerPot(temp),
                    new BlockImpl("tainted_gold_block", Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3F)),
                    new BlockNetherGrass(),
                    new BlockLight(),
                    new BlockChorusGenerator(),
                    new BlockAuraTimer(),
                    new BlockSlimeSplitGenerator(),
                    new BlockSpring(),
                    new BlockWeatherChanger(),
                    new BlockRFConverter(),
                    new BlockChunkLoader());
            Helper.populateObjectHolders(ModBlocks.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.ITEMS, h -> {
            for (var block : ModRegistry.ALL_ITEMS) {
                if (block instanceof Block && !(block instanceof INoItemBlock)) {
                    var item = new BlockItem((Block) block, new Item.Properties().tab(NaturesAura.CREATIVE_TAB));
                    h.register(new ResourceLocation(NaturesAura.MOD_ID, block.getBaseName()), item);
                }
            }

            Item temp;
            ModRegistry.registerAll(h,
                    new ItemPickaxe("infused_iron_pickaxe", ModItemTier.INFUSED, 1, -2.8F),
                    new ItemAxe("infused_iron_axe", ModItemTier.INFUSED, 6.0F, -3.1F),
                    new ItemShovel("infused_iron_shovel", ModItemTier.INFUSED, 1.5F, -3.0F),
                    new ItemHoe("infused_iron_hoe", ModItemTier.INFUSED, -1),
                    new ItemSword("infused_iron_sword", ModItemTier.INFUSED, 3, -2.4F),
                    new ItemArmor("infused_iron_helmet", ModArmorMaterial.INFUSED, EquipmentSlot.HEAD),
                    new ItemArmor("infused_iron_chest", ModArmorMaterial.INFUSED, EquipmentSlot.CHEST),
                    new ItemArmor("infused_iron_pants", ModArmorMaterial.INFUSED, EquipmentSlot.LEGS),
                    new ItemArmor("infused_iron_shoes", ModArmorMaterial.INFUSED, EquipmentSlot.FEET),
                    new ItemEye("eye"),
                    new ItemEye("eye_improved"),
                    new ItemGoldFiber(),
                    new ItemImpl("gold_leaf"),
                    new ItemImpl("infused_iron"),
                    new ItemImpl("ancient_stick"),
                    new ItemColorChanger(),
                    new ItemAuraCache("aura_cache", 400000),
                    new ItemAuraCache("aura_trove", 1200000),
                    new ItemShockwaveCreator(),
                    new ItemMultiblockMaker(),
                    temp = new ItemImpl("bottle_two_the_rebottling"),
                    new ItemAuraBottle(temp),
                    new ItemImpl("farming_stencil"),
                    new ItemImpl("sky_ingot"),
                    new ItemGlowing("calling_spirit"),
                    new ItemEffectPowder(),
                    new ItemBirthSpirit(),
                    new ItemMoverMinecart(),
                    new ItemRangeVisualizer(),
                    new ItemImpl("clock_hand"),
                    new ItemImpl("token_joy"),
                    new ItemImpl("token_fear"),
                    new ItemImpl("token_anger"),
                    new ItemImpl("token_sorrow"),
                    new ItemImpl("token_euphoria"),
                    new ItemImpl("token_terror"),
                    new ItemImpl("token_rage"),
                    new ItemImpl("token_grief"),
                    new ItemEnderAccess(),
                    new ItemCaveFinder(),
                    new ItemCrimsonMeal(),
                    new ItemDeathRing(),
                    new ItemImpl("tainted_gold"),
                    new ItemLootFinder(),
                    new ItemLightStaff(),
                    new ItemPickaxe("sky_pickaxe", ModItemTier.SKY, 1, -2.8F),
                    new ItemAxe("sky_axe", ModItemTier.SKY, 5.0F, -3.0F),
                    new ItemShovel("sky_shovel", ModItemTier.SKY, 1.5F, -3.0F),
                    new ItemHoe("sky_hoe", ModItemTier.SKY, -1),
                    new ItemSword("sky_sword", ModItemTier.SKY, 3, -2.4F),
                    new ItemArmor("sky_helmet", ModArmorMaterial.SKY, EquipmentSlot.HEAD),
                    new ItemArmor("sky_chest", ModArmorMaterial.SKY, EquipmentSlot.CHEST),
                    new ItemArmor("sky_pants", ModArmorMaterial.SKY, EquipmentSlot.LEGS),
                    new ItemArmor("sky_shoes", ModArmorMaterial.SKY, EquipmentSlot.FEET),
                    new ItemStructureFinder("fortress_finder", Structures.FORTRESS, 0xba2800, 1024),
                    new ItemStructureFinder("end_city_finder", Structures.END_CITY, 0xca5cd6, 1024),
                    new ItemStructureFinder("outpost_finder", Structures.PILLAGER_OUTPOST, 0xab9f98, 2048),
                    new ItemBreakPrevention(),
                    new ItemPetReviver(),
                    new ItemNetheriteFinder()
            );
            Helper.populateObjectHolders(ModItems.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, h -> {
            // add tile entities that support multiple blocks
            ModRegistry.ALL_ITEMS.add(new ModTileType<>(BlockEntityAuraBloom::new, "aura_bloom", ModRegistry.ALL_ITEMS.stream().filter(i -> i instanceof BlockAuraBloom).toArray(IModItem[]::new)));

            for (var item : ModRegistry.ALL_ITEMS) {
                if (item instanceof ModTileType type)
                    h.register(new ResourceLocation(NaturesAura.MOD_ID, type.getBaseName()), type.type);
            }
            Helper.populateObjectHolders(ModBlockEntities.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.MOB_EFFECTS, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "breathless"), new PotionBreathless());
            Helper.populateObjectHolders(ModPotions.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.MENU_TYPES, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "ender_crate"), IForgeMenuType.create((windowId, inv, data) -> {
                var tile = inv.player.level.getBlockEntity(data.readBlockPos());
                if (tile instanceof BlockEntityEnderCrate crate)
                    return new ContainerEnderCrate(ModContainers.ENDER_CRATE, windowId, inv.player, crate.getItemHandler());
                return null;
            }));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "ender_access"), IForgeMenuType.create((windowId, inv, data) -> {
                IItemHandler handler = ILevelData.getOverworldData(inv.player.level).getEnderStorage(data.readUtf());
                return new ContainerEnderCrate(ModContainers.ENDER_ACCESS, windowId, inv.player, handler);
            }));
            Helper.populateObjectHolders(ModContainers.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.ENCHANTMENTS, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "aura_mending"), new AuraMendingEnchantment());
            Helper.populateObjectHolders(ModEnchantments.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.ENTITY_TYPES, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "mover_cart"), EntityType.Builder
                    .of(EntityMoverMinecart::new, MobCategory.MISC)
                    .sized(1, 1).setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(3).fireImmune().build(NaturesAura.MOD_ID + ":mover_minecart"));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "effect_inhibitor"), EntityType.Builder
                    .of(EntityEffectInhibitor::new, MobCategory.MISC)
                    .sized(1, 1).setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(20).fireImmune().build(NaturesAura.MOD_ID + ":effect_inhibitor"));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "light_projectile"), EntityType.Builder
                    .<EntityLightProjectile>of(EntityLightProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(3).fireImmune().build(NaturesAura.MOD_ID + ":light_projectile"));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "structure_finder"), EntityType.Builder
                    .of(EntityStructureFinder::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(2).fireImmune().build(NaturesAura.MOD_ID + ":structure_finder"));
            Helper.populateObjectHolders(ModEntities.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.FEATURES, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "aura_bloom"), new LevelGenAuraBloom(ModBlocks.AURA_BLOOM, 60, false));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "aura_cactus"), new LevelGenAuraBloom(ModBlocks.AURA_CACTUS, 60, false));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "warped_aura_mushroom"), new LevelGenAuraBloom(ModBlocks.WARPED_AURA_MUSHROOM, 10, true));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "crimson_aura_mushroom"), new LevelGenAuraBloom(ModBlocks.CRIMSON_AURA_MUSHROOM, 10, true));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "aura_mushroom"), new LevelGenAuraBloom(ModBlocks.AURA_MUSHROOM, 20, false));
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "ancient_tree"), new LevelGenAncientTree());
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "nether_wart_mushroom"), new LevelGenNetherWartMushroom());
            Helper.populateObjectHolders(ModFeatures.class, event.getForgeRegistry());
        });

        event.register(ForgeRegistries.Keys.RECIPE_TYPES, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "altar"), ModRecipes.ALTAR_TYPE);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "animal_spawner"), ModRecipes.ANIMAL_SPAWNER_TYPE);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "offering"), ModRecipes.OFFERING_TYPE);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"), ModRecipes.TREE_RITUAL_TYPE);
        });

        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, h -> {
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "altar"), ModRecipes.ALTAR_SERIALIZER);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "animal_spawner"), ModRecipes.ANIMAL_SPAWNER_SERIALIZER);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "offering"), ModRecipes.OFFERING_SERIALIZER);
            h.register(new ResourceLocation(NaturesAura.MOD_ID, "tree_ritual"), ModRecipes.TREE_RITUAL_SERIALIZER);
            CraftingHelper.register(new EnabledCondition.Serializer());
        });
    }

    public static void init() {
        ModFeatures.Configured.AURA_BLOOM = FeatureUtils.register(NaturesAura.MOD_ID + ":aura_bloom", ModFeatures.AURA_BLOOM, NoneFeatureConfiguration.INSTANCE);
        ModFeatures.Configured.AURA_CACTUS = FeatureUtils.register(NaturesAura.MOD_ID + ":aura_cactus", ModFeatures.AURA_CACTUS, NoneFeatureConfiguration.INSTANCE);
        ModFeatures.Configured.WARPED_AURA_MUSHROOM = FeatureUtils.register(NaturesAura.MOD_ID + ":warped_aura_mushroom", ModFeatures.WARPED_AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
        ModFeatures.Configured.CRIMSON_AURA_MUSHROOM = FeatureUtils.register(NaturesAura.MOD_ID + ":crimson_aura_mushroom", ModFeatures.CRIMSON_AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
        ModFeatures.Configured.AURA_MUSHROOM = FeatureUtils.register(NaturesAura.MOD_ID + ":aura_mushroom", ModFeatures.AURA_MUSHROOM, NoneFeatureConfiguration.INSTANCE);
        ModFeatures.Configured.ANCIENT_TREE = FeatureUtils.register(NaturesAura.MOD_ID + ":ancient_tree", ModFeatures.ANCIENT_TREE, new TreeConfiguration.TreeConfigurationBuilder(null, null, null, null, null).build());
        ModFeatures.Configured.NETHER_WART_MUSHROOM = FeatureUtils.register(NaturesAura.MOD_ID + ":nether_wart_mushroom", ModFeatures.NETHER_WART_MUSHROOM, NoneFeatureConfiguration.INSTANCE);

        ModFeatures.Placed.AURA_BLOOM = PlacementUtils.register(NaturesAura.MOD_ID + ":aura_bloom", ModFeatures.Configured.AURA_BLOOM);
        ModFeatures.Placed.AURA_CACTUS = PlacementUtils.register(NaturesAura.MOD_ID + ":aura_cactus", ModFeatures.Configured.AURA_CACTUS);
        ModFeatures.Placed.WARPED_AURA_MUSHROOM = PlacementUtils.register(NaturesAura.MOD_ID + ":warped_aura_mushroom", ModFeatures.Configured.WARPED_AURA_MUSHROOM);
        ModFeatures.Placed.CRIMSON_AURA_MUSHROOM = PlacementUtils.register(NaturesAura.MOD_ID + ":crimson_aura_mushroom", ModFeatures.Configured.CRIMSON_AURA_MUSHROOM);
        ModFeatures.Placed.AURA_MUSHROOM = PlacementUtils.register(NaturesAura.MOD_ID + ":aura_mushroom", ModFeatures.Configured.AURA_MUSHROOM);
    }

    public static Block createFlowerPot(Block block) {
        var props = Block.Properties.of(Material.DECORATION).strength(0F);
        Block potted = new BlockFlowerPot(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> block, props);
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ForgeRegistries.BLOCKS.getKey(block), () -> potted);
        return potted;
    }

    @SafeVarargs
    private static <T> void registerAll(RegisterEvent.RegisterHelper<T> helper, T... items) {
        for (var item : items)
            helper.register(new ResourceLocation(NaturesAura.MOD_ID, ((IModItem) item).getBaseName()), item);
    }
}
