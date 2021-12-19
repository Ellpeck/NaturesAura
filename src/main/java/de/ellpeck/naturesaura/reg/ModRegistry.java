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
import de.ellpeck.naturesaura.entities.render.RenderEffectInhibitor;
import de.ellpeck.naturesaura.entities.render.RenderMoverMinecart;
import de.ellpeck.naturesaura.entities.render.RenderStub;
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
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModRegistry {

    public static final Set<IModItem> ALL_ITEMS = new HashSet<>();

    public static void add(IModItem item) {
        ALL_ITEMS.add(item);
        item.getRegistryEntry().setRegistryName(item.getBaseName());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Block temp;
        event.getRegistry().registerAll(
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
                createFlowerPot(temp),
                temp = new BlockAuraBloom("aura_cactus", Blocks.SAND, Blocks.RED_SAND),
                createFlowerPot(temp),
                temp = new BlockAuraBloom("warped_aura_mushroom", Blocks.WARPED_NYLIUM),
                createFlowerPot(temp),
                temp = new BlockAuraBloom("crimson_aura_mushroom", Blocks.CRIMSON_NYLIUM),
                createFlowerPot(temp),
                temp = new BlockAuraBloom("aura_mushroom", Blocks.MYCELIUM),
                createFlowerPot(temp),
                new BlockImpl("tainted_gold_block", Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3F)),
                new BlockNetherGrass(),
                new BlockLight(),
                new BlockChorusGenerator(),
                new BlockAuraTimer(),
                new BlockSlimeSplitGenerator(),
                new BlockSpring(),
                new BlockWeatherChanger(),
                new BlockRFConverter(),
                new BlockChunkLoader()
        );
        Helper.populateObjectHolders(ModBlocks.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (var block : ALL_ITEMS) {
            if (block instanceof Block && !(block instanceof INoItemBlock)) {
                var item = new BlockItem((Block) block, new Item.Properties().tab(NaturesAura.CREATIVE_TAB));
                item.setRegistryName(block.getBaseName());
                event.getRegistry().register(item);
            }
        }

        Item temp;
        event.getRegistry().registerAll(
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
                new ItemStructureFinder("fortress_finder", StructureFeature.NETHER_BRIDGE, 0xba2800, 1024),
                new ItemStructureFinder("end_city_finder", StructureFeature.END_CITY, 0xca5cd6, 1024),
                new ItemStructureFinder("outpost_finder", StructureFeature.PILLAGER_OUTPOST, 0xab9f98, 2048),
                new ItemBreakPrevention(),
                new ItemPetReviver(),
                new ItemNetheriteFinder()
        );
        Helper.populateObjectHolders(ModItems.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
        // add tile entities that support multiple blocks
        add(new ModTileType<>(BlockEntityAuraBloom::new, "aura_bloom", ALL_ITEMS.stream().filter(i -> i instanceof BlockAuraBloom).toArray(IModItem[]::new)));

        for (var item : ALL_ITEMS) {
            if (item instanceof ModTileType type)
                event.getRegistry().register(type.type);
        }
        Helper.populateObjectHolders(ModBlockEntities.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().registerAll(
                new PotionBreathless()
        );
        Helper.populateObjectHolders(ModPotions.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(
                IForgeMenuType.create((windowId, inv, data) -> {
                    var tile = inv.player.level.getBlockEntity(data.readBlockPos());
                    if (tile instanceof BlockEntityEnderCrate crate)
                        return new ContainerEnderCrate(ModContainers.ENDER_CRATE, windowId, inv.player, crate.getItemHandler());
                    return null;
                }).setRegistryName("ender_crate"),
                IForgeMenuType.create((windowId, inv, data) -> {
                    IItemHandler handler = ILevelData.getOverworldData(inv.player.level).getEnderStorage(data.readUtf());
                    return new ContainerEnderCrate(ModContainers.ENDER_ACCESS, windowId, inv.player, handler);
                }).setRegistryName("ender_access")
        );
        Helper.populateObjectHolders(ModContainers.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(
                new AuraMendingEnchantment()
        );
        Helper.populateObjectHolders(ModEnchantments.class, event.getRegistry());
    }

    @SuppressWarnings("Convert2Lambda")
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.of(EntityMoverMinecart::new, MobCategory.MISC)
                        .sized(1, 1).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(3).fireImmune().build(NaturesAura.MOD_ID + ":mover_minecart")
                        .setRegistryName("mover_cart"),
                EntityType.Builder.of(EntityEffectInhibitor::new, MobCategory.MISC)
                        .sized(1, 1).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(20).fireImmune().build(NaturesAura.MOD_ID + ":effect_inhibitor")
                        .setRegistryName("effect_inhibitor"),
                EntityType.Builder.<EntityLightProjectile>of(EntityLightProjectile::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(3).fireImmune().build(NaturesAura.MOD_ID + ":light_projectile")
                        .setRegistryName("light_projectile"),
                EntityType.Builder.of(EntityStructureFinder::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(2).fireImmune().build(NaturesAura.MOD_ID + ":structure_finder")
                        .setRegistryName("structure_finder")
        );
        Helper.populateObjectHolders(ModEntities.class, event.getRegistry());

        NaturesAura.proxy.registerEntityRenderer(ModEntities.MOVER_CART, () -> RenderMoverMinecart::new);
        NaturesAura.proxy.registerEntityRenderer(ModEntities.EFFECT_INHIBITOR, () -> RenderEffectInhibitor::new);
        NaturesAura.proxy.registerEntityRenderer(ModEntities.LIGHT_PROJECTILE, () -> RenderStub::new);
        // for some reason, only this one causes classloading issues if shortened to a lambda, what
        NaturesAura.proxy.registerEntityRenderer(ModEntities.STRUCTURE_FINDER, () -> new EntityRendererProvider<>() {
            @Override
            public EntityRenderer<EntityStructureFinder> create(Context context) {
                return new ThrownItemRenderer<>(context, 1, true);
            }
        });
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(
                new LevelGenAuraBloom(ModBlocks.AURA_BLOOM, 60, false).setRegistryName("aura_bloom"),
                new LevelGenAuraBloom(ModBlocks.AURA_CACTUS, 60, false).setRegistryName("aura_cactus"),
                new LevelGenAuraBloom(ModBlocks.WARPED_AURA_MUSHROOM, 10, true).setRegistryName("warped_aura_mushroom"),
                new LevelGenAuraBloom(ModBlocks.CRIMSON_AURA_MUSHROOM, 10, true).setRegistryName("crimson_aura_mushroom"),
                new LevelGenAuraBloom(ModBlocks.AURA_MUSHROOM, 20, false).setRegistryName("aura_mushroom"),
                new LevelGenAncientTree().setRegistryName("ancient_tree"),
                new LevelGenNetherWartMushroom().setRegistryName("nether_wart_mushroom")
        );
        Helper.populateObjectHolders(ModFeatures.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<RecipeSerializer<?>> event) {
        ModRecipes.register(event.getRegistry());
        CraftingHelper.register(new EnabledCondition.Serializer());
    }

    public static void init() {
        for (var item : ALL_ITEMS) {
            if (item instanceof IColorProvidingBlock)
                NaturesAura.proxy.addColorProvidingBlock((IColorProvidingBlock) item);
            if (item instanceof IColorProvidingItem)
                NaturesAura.proxy.addColorProvidingItem((IColorProvidingItem) item);
            if (item instanceof ITESRProvider provider)
                provider.registerTESR();
        }

        // register features 27 more times for some reason
        for (var entry : ModFeatures.Configured.class.getFields()) {
            try {
                var feature = (ConfiguredFeature<?, ?>) entry.get(null);
                Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, feature.feature.getRegistryName(), feature);
            } catch (IllegalAccessException e) {
                NaturesAura.LOGGER.error(e);
            }
        }
        for (var entry : ModFeatures.Placed.class.getFields()) {
            try {
                var placed = (PlacedFeature) entry.get(null);
                // why are you making this so difficult for me
                Supplier<ConfiguredFeature<?, ?>> feature = ObfuscationReflectionHelper.getPrivateValue(PlacedFeature.class, placed, "f_191775_");
                Registry.register(BuiltinRegistries.PLACED_FEATURE, feature.get().feature.getRegistryName(), placed);
            } catch (IllegalAccessException e) {
                NaturesAura.LOGGER.error(e);
            }
        }
    }

    public static Block createFlowerPot(Block block) {
        var props = Block.Properties.of(Material.DECORATION).strength(0F);
        Block potted = new BlockFlowerPot(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> block, props);
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(block.getRegistryName(), () -> potted);
        return potted;
    }
}
