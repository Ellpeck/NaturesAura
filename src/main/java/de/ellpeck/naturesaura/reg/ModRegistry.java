package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.*;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraBloom;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraBloom.TileEntityAuraCactus;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityEnderCrate;
import de.ellpeck.naturesaura.enchant.AuraMendingEnchantment;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.entities.EntityLightProjectile;
import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import de.ellpeck.naturesaura.entities.ModEntities;
import de.ellpeck.naturesaura.entities.render.RenderEffectInhibitor;
import de.ellpeck.naturesaura.entities.render.RenderMoverMinecart;
import de.ellpeck.naturesaura.entities.render.RenderStub;
import de.ellpeck.naturesaura.gen.ModFeatures;
import de.ellpeck.naturesaura.gen.WorldGenAncientTree;
import de.ellpeck.naturesaura.gen.WorldGenAuraBloom;
import de.ellpeck.naturesaura.gen.WorldGenNetherWartMushroom;
import de.ellpeck.naturesaura.gui.ContainerEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import de.ellpeck.naturesaura.items.*;
import de.ellpeck.naturesaura.items.tools.*;
import de.ellpeck.naturesaura.potion.ModPotions;
import de.ellpeck.naturesaura.potion.PotionBreathless;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

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
                temp = new BlockImpl("ancient_planks", ModBlocks.prop(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2F)),
                new BlockStairsNA("ancient_stairs", "ancient_planks", temp::getDefaultState, ModBlocks.prop(temp)),
                new Slab("ancient_slab", "ancient_planks", ModBlocks.prop(temp)),
                new BlockAncientLeaves(),
                new BlockAncientSapling(),
                new BlockNatureAltar(),
                new BlockDecayedLeaves(),
                new BlockGoldenLeaves(),
                new BlockGoldPowder(),
                new BlockWoodStand(),
                temp = new BlockImpl("infused_stone", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.75F)),
                new BlockStairsNA("infused_stairs", "infused_stone", temp::getDefaultState, ModBlocks.prop(temp)),
                new Slab("infused_slab", "infused_stone", ModBlocks.prop(temp)),
                temp = new BlockImpl("infused_brick", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5F)),
                new BlockStairsNA("infused_brick_stairs", "infused_brick", temp::getDefaultState, ModBlocks.prop(temp)),
                new Slab("infused_brick_slab", "infused_brick", ModBlocks.prop(temp)),
                new BlockFurnaceHeater(),
                new BlockPotionGenerator(),
                new BlockAuraDetector(),
                new BlockCatalyst("conversion_catalyst", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.5F)),
                new BlockCatalyst("crushing_catalyst", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.5F)),
                new BlockFlowerGenerator(),
                new BlockPlacer(),
                new BlockHopperUpgrade(),
                new BlockFieldCreator(),
                new BlockOakGenerator(),
                new BlockImpl("infused_iron_block", ModBlocks.prop(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3F)),
                new BlockOfferingTable(),
                new BlockPickupStopper(),
                new BlockSpawnLamp(),
                new BlockAnimalGenerator(),
                new BlockEndFlower(),
                new BlockGratedChute(),
                new BlockAnimalSpawner(),
                new BlockAutoCrafter(),
                new BlockImpl("gold_brick", ModBlocks.prop(Blocks.STONE_BRICKS)),
                new BlockImpl("gold_nether_brick", ModBlocks.prop(Blocks.NETHER_BRICKS)),
                new BlockMossGenerator(),
                new BlockTimeChanger(),
                new BlockGeneratorLimitRemover(),
                new BlockEnderCrate(),
                new BlockPowderPlacer(),
                new BlockFireworkGenerator(),
                new BlockProjectileGenerator(),
                new BlockDimensionRail("overworld", DimensionType.OVERWORLD, DimensionType.THE_NETHER, DimensionType.THE_END),
                new BlockDimensionRail("nether", DimensionType.THE_NETHER, DimensionType.OVERWORLD),
                new BlockDimensionRail("end", DimensionType.THE_END, DimensionType.OVERWORLD),
                new BlockBlastFurnaceBooster(),
                new BlockImpl("nether_wart_mushroom", ModBlocks.prop(Blocks.RED_MUSHROOM_BLOCK)),
                new BlockAnimalContainer(),
                new BlockSnowCreator(),
                new BlockItemDistributor(),
                temp = new BlockAuraBloom("aura_bloom", TileEntityAuraBloom::new),
                createFlowerPot(temp),
                temp = new BlockAuraBloom("aura_cactus", TileEntityAuraCactus::new),
                createFlowerPot(temp),
                new BlockImpl("tainted_gold_block", ModBlocks.prop(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3F)),
                new BlockNetherGrass(),
                new BlockLight(),
                new BlockChorusGenerator(),
                new BlockAuraTimer()
        );

        if (ModConfig.instance.rfConverter.get())
            event.getRegistry().register(new BlockRFConverter());
        if (ModConfig.instance.chunkLoader.get())
            event.getRegistry().register(new BlockChunkLoader());

        Helper.populateObjectHolders(ModBlocks.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (IModItem block : ALL_ITEMS) {
            if (block instanceof Block && !(block instanceof INoItemBlock)) {
                BlockItem item = new BlockItem((Block) block, new Item.Properties().group(NaturesAura.CREATIVE_TAB));
                item.setRegistryName(block.getBaseName());
                event.getRegistry().register(item);
            }
        }

        Item temp;
        event.getRegistry().registerAll(
                new ItemPickaxe("infused_iron_pickaxe", ModItemTier.INFUSED, 1, -2.8F),
                new ItemAxe("infused_iron_axe", ModItemTier.INFUSED, 6.0F, -3.1F),
                new ItemShovel("infused_iron_shovel", ModItemTier.INFUSED, 1.5F, -3.0F),
                new ItemHoe("infused_iron_hoe", ModItemTier.INFUSED, -1.0F),
                new ItemSword("infused_iron_sword", ModItemTier.INFUSED, 3, -2.4F),
                new ItemArmor("infused_iron_helmet", ModArmorMaterial.INFUSED, EquipmentSlotType.HEAD),
                new ItemArmor("infused_iron_chest", ModArmorMaterial.INFUSED, EquipmentSlotType.CHEST),
                new ItemArmor("infused_iron_pants", ModArmorMaterial.INFUSED, EquipmentSlotType.LEGS),
                new ItemArmor("infused_iron_shoes", ModArmorMaterial.INFUSED, EquipmentSlotType.FEET),
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
                new ItemLightStaff()
        );
        Helper.populateObjectHolders(ModItems.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof ModTileType)
                event.getRegistry().register(((ModTileType) item).type);
        }
        Helper.populateObjectHolders(ModTileEntities.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Effect> event) {
        event.getRegistry().registerAll(
                new PotionBreathless()
        );
        Helper.populateObjectHolders(ModPotions.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                IForgeContainerType.create((windowId, inv, data) -> {
                    TileEntity tile = inv.player.world.getTileEntity(data.readBlockPos());
                    if (tile instanceof TileEntityEnderCrate)
                        return new ContainerEnderCrate(ModContainers.ENDER_CRATE, windowId, inv.player, ((TileEntityEnderCrate) tile).getItemHandler(null));
                    return null;
                }).setRegistryName("ender_crate"),
                IForgeContainerType.create((windowId, inv, data) -> {
                    IItemHandler handler = IWorldData.getOverworldData(inv.player.world).getEnderStorage(data.readString());
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

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create(EntityMoverMinecart::new, EntityClassification.MISC)
                        .size(1, 1).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(3).immuneToFire().build(NaturesAura.MOD_ID + ":mover_minecart")
                        .setRegistryName("mover_cart"),
                EntityType.Builder.create(EntityEffectInhibitor::new, EntityClassification.MISC)
                        .size(1, 1).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(20).immuneToFire().build(NaturesAura.MOD_ID + ":effect_inhibitor")
                        .setRegistryName("effect_inhibitor"),
                EntityType.Builder.<EntityLightProjectile>create(EntityLightProjectile::new, EntityClassification.MISC)
                        .size(0.5F, 0.5F).setShouldReceiveVelocityUpdates(true)
                        .setTrackingRange(64).setUpdateInterval(3).immuneToFire().build(NaturesAura.MOD_ID + ":light_projectile")
                        .setRegistryName("light_projectile")
        );
        Helper.populateObjectHolders(ModEntities.class, event.getRegistry());

        NaturesAura.proxy.registerEntityRenderer(ModEntities.MOVER_CART, () -> RenderMoverMinecart::new);
        NaturesAura.proxy.registerEntityRenderer(ModEntities.EFFECT_INHIBITOR, () -> RenderEffectInhibitor::new);
        NaturesAura.proxy.registerEntityRenderer(ModEntities.LIGHT_PROJECTILE, () -> RenderStub::new);
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().registerAll(
                new WorldGenAuraBloom(ModBlocks.AURA_BLOOM).setRegistryName("aura_bloom"),
                new WorldGenAuraBloom(ModBlocks.AURA_CACTUS).setRegistryName("aura_cactus"),
                new WorldGenAncientTree().setRegistryName("ancient_tree"),
                new WorldGenNetherWartMushroom().setRegistryName("nether_wart_mushroom")
        );
        Helper.populateObjectHolders(ModFeatures.class, event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        ModRecipes.register(event.getRegistry());
    }

    public static void init() {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof IColorProvidingBlock)
                NaturesAura.proxy.addColorProvidingBlock((IColorProvidingBlock) item);
            if (item instanceof IColorProvidingItem)
                NaturesAura.proxy.addColorProvidingItem((IColorProvidingItem) item);
            if (item instanceof ITESRProvider)
                NaturesAura.proxy.registerTESR((ITESRProvider) item);
        }

        for (Biome biome : ForgeRegistries.BIOMES) {
            if (ModConfig.instance.auraBlooms.get()) {
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.AURA_BLOOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
                if (biome.getCategory() == Biome.Category.DESERT)
                    biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.AURA_CACTUS.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG)));
            }
        }
    }

    public static Block createFlowerPot(Block block) {
        Block.Properties props = Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0F);
        Block potted = new BlockFlowerPot(() -> (FlowerPotBlock) Blocks.FLOWER_POT, block::getBlock, props);
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(block.getRegistryName(), () -> potted);
        return potted;
    }
}
