package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.*;
import de.ellpeck.naturesaura.items.*;
import de.ellpeck.naturesaura.items.tools.*;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public final class ModRegistry {

    private static final List<IModItem> ALL_ITEMS = new ArrayList<>();

    public static void add(IModItem item) {
        ALL_ITEMS.add(item);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockAncientLog("ancient_log"),
                new BlockAncientLog("ancient_bark"),
                new BlockImpl("ancient_planks", ModBlocks.prop(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(2F)),
                new BlockStairsNA("ancient_stairs", ModBlocks.ANCIENT_PLANKS::getDefaultState, ModBlocks.prop(ModBlocks.ANCIENT_PLANKS)),
                new Slab("ancient_slab", ModBlocks.prop(ModBlocks.ANCIENT_PLANKS)),
                new BlockAncientLeaves(),
                new BlockAncientSapling(),
                new BlockNatureAltar(),
                new BlockDecayedLeaves(),
                new BlockGoldenLeaves(),
                new BlockGoldPowder(),
                new BlockWoodStand(),
                new BlockImpl("infused_stone", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.75F)),
                new BlockStairsNA("infused_stairs", ModBlocks.INFUSED_STONE::getDefaultState, ModBlocks.prop(ModBlocks.INFUSED_STONE)),
                new Slab("infused_slab", ModBlocks.prop(ModBlocks.INFUSED_STONE)),
                new BlockImpl("infused_brick", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5F)),
                new BlockStairsNA("infused_brick_stairs", ModBlocks.INFUSED_BRICK::getDefaultState, ModBlocks.prop(ModBlocks.INFUSED_BRICK)),
                new Slab("infused_brick_slab", ModBlocks.prop(ModBlocks.INFUSED_BRICK)),
                new BlockFurnaceHeater(),
                new BlockPotionGenerator(),
                new BlockAuraDetector(),
                new BlockImpl("conversion_catalyst", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.5F)),
                new BlockImpl("crushing_catalyst", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.5F)),
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
                new BlockImpl("gold_brick", ModBlocks.prop(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2F)),
                new BlockMossGenerator(),
                new BlockTimeChanger(),
                new BlockGeneratorLimitRemover(),
                new BlockEnderCrate(),
                new BlockPowderPlacer(),
                new BlockFireworkGenerator(),
                new BlockProjectileGenerator(),
                new BlockDimensionRail("overworld", DimensionType.OVERWORLD, DimensionType.THE_NETHER, DimensionType.THE_END),
                new BlockDimensionRail("nether", DimensionType.THE_NETHER, DimensionType.OVERWORLD),
                new BlockDimensionRail("end", DimensionType.THE_END, DimensionType.OVERWORLD)
        );

        if (ModConfig.enabledFeatures.rfConverter)
            event.getRegistry().register(new BlockRFConverter());
        if (ModConfig.enabledFeatures.chunkLoader)
            event.getRegistry().register(new BlockChunkLoader());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof Block) {
                if (item instanceof ICustomItemBlockProvider) {
                    event.getRegistry().register(((ICustomItemBlockProvider) item).getItemBlock());
                } else {
                    event.getRegistry().register(new BlockItem((Block) item, new Item.Properties()));
                }
            }
        }

        event.getRegistry().registerAll(
                new Pickaxe("infused_iron_pickaxe", NAItemTier.INFUSED, 8, 3.2F),
                new Axe("infused_iron_axe", NAItemTier.INFUSED, 8.25F, 3.2F),
                new Shovel("infused_iron_shovel", NAItemTier.INFUSED, 8.25F, 3.2F),
                new Hoe("infused_iron_hoe", NAItemTier.INFUSED, 3.2F),
                new Sword("infused_iron_sword", NAItemTier.INFUSED, 3, 3), // TODO dmg and speed values need to be changed
                new Armor("infused_iron_helmet", NAArmorMaterial.INFUSED, EquipmentSlotType.HEAD),
                new Armor("infused_iron_chest", NAArmorMaterial.INFUSED, EquipmentSlotType.CHEST),
                new Armor("infused_iron_pants", NAArmorMaterial.INFUSED, EquipmentSlotType.LEGS),
                new Armor("infused_iron_shoes", NAArmorMaterial.INFUSED, EquipmentSlotType.FEET),
                new Eye("eye"),
                new Eye("eye_improved"),
                new GoldFiber(),
                new ItemImpl("gold_leaf"),
                new ItemImpl("infused_iron"),
                new ItemImpl("ancient_stick"),
                new ColorChanger(),
                new AuraCache("aura_cache", 400000),
                new AuraCache("aura_trove", 1200000),
                new ShockwaveCreator(),
                new MultiblockMaker(),
                new ItemImpl("bottle_two_the_rebottling"),
                new AuraBottle(),
                new ItemImpl("farming_stencil"),
                new ItemImpl("sky_ingot"),
                new Glowing("calling_spirit"),
                new EffectPowder(),
                new BirthSpirit(),
                new MoverMinecart(),
                new RangeVisualizer(),
                new ItemImpl("clock_hand"),
                new ItemImpl("token_joy"),
                new ItemImpl("token_fear"),
                new ItemImpl("token_anger"),
                new ItemImpl("token_sorrow"),
                new ItemImpl("token_euphoria"),
                new ItemImpl("token_terror"),
                new ItemImpl("token_rage"),
                new ItemImpl("token_grief"),
                new EnderAccess(),
                new CaveFinder()
        );
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {

    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Effect> event) {

    }
    
/*
    private static void registerPotion(Effect potion, String name) {
        potion.setRegistryName("potion." + NaturesAura.MOD_ID + "." + name + ".name");

        potion.setRegistryName(NaturesAura.MOD_ID, name);
        ForgeRegistries.POTIONS.register(potion);
    }

    private static void registerItem(Item item, String name, ItemGroup tab) {
        item.setRegistryName(NaturesAura.MOD_ID, name);
        ForgeRegistries.ITEMS.register(item);

        item.setCreativeTab(tab);
    }

    private static void registerBlock(Block block, String name, BlockItem item, ItemGroup tab) {
        block.setTranslationKey(NaturesAura.MOD_ID + "." + name);

        block.setRegistryName(NaturesAura.MOD_ID, name);
        ForgeRegistries.BLOCKS.register(block);

        if (item != null) {
            item.setRegistryName(block.getRegistryName());
            ForgeRegistries.ITEMS.register(item);
        }

        block.setCreativeTab(tab);
    }

    private static ItemGroup getTab(IModItem item) {
        if (item instanceof ICreativeItem) {
            return ((ICreativeItem) item).getGroupToAdd();
        }
        return null;
    }

    public static void preInit(FMLCommonSetupEvent event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof Item) {
                registerItem((Item) item, item.getBaseName(), getTab(item));
            } else if (item instanceof Block) {
                Block block = (Block) item;

                BlockItem itemBlock;
                if (item instanceof ICustomItemBlockProvider) {
                    itemBlock = ((ICustomItemBlockProvider) item).getItemBlock();
                } else {
                    itemBlock = new BlockItem(block);
                }

                registerBlock(block, item.getBaseName(), itemBlock, getTab(item));
            } else if (item instanceof Effect) {
                registerPotion((Effect) item, item.getBaseName());
            }

            if (item instanceof IModelProvider) {
                Map<ItemStack, ModelResourceLocation> models = ((IModelProvider) item).getModelLocations();
                for (ItemStack stack : models.keySet())
                    NaturesAura.proxy.registerRenderer(stack, models.get(stack));
            }

            item.onPreInit(event);
        }
    }
*/

    public static void init(FMLCommonSetupEvent event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof IColorProvidingBlock) {
                NaturesAura.proxy.addColorProvidingBlock((IColorProvidingBlock) item);
            }
            if (item instanceof IColorProvidingItem) {
                NaturesAura.proxy.addColorProvidingItem((IColorProvidingItem) item);
            }
            if (item instanceof ITESRProvider) {
                NaturesAura.proxy.registerTESR((ITESRProvider) item);
            }
        }
    }
}
