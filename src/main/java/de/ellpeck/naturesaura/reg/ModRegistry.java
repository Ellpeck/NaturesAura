package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ModRegistry {

    private static final List<IModItem> ALL_ITEMS = new ArrayList<>();

    public static void add(IModItem item) {
        ALL_ITEMS.add(item);
    }

    private static void registerPotion(Effect potion, String name) {
        potion.setPotionName("potion." + NaturesAura.MOD_ID + "." + name + ".name");

        potion.setRegistryName(NaturesAura.MOD_ID, name);
        ForgeRegistries.POTIONS.register(potion);
    }

    private static void registerItem(Item item, String name, ItemGroup tab) {
        item.setTranslationKey(NaturesAura.MOD_ID + "." + name);

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
        if (item instanceof ICreativeItem)
            return ((ICreativeItem) item).getTabToAdd();
        return null;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof Item) {
                registerItem((Item) item, item.getBaseName(), getTab(item));
            } else if (item instanceof Block) {
                Block block = (Block) item;

                BlockItem itemBlock;
                if (item instanceof ICustomItemBlockProvider)
                    itemBlock = ((ICustomItemBlockProvider) item).getItemBlock();
                else
                    itemBlock = new BlockItem(block);

                registerBlock(block, item.getBaseName(), itemBlock, getTab(item));
            } else if (item instanceof Effect)
                registerPotion((Effect) item, item.getBaseName());

            if (item instanceof IModelProvider) {
                Map<ItemStack, ModelResourceLocation> models = ((IModelProvider) item).getModelLocations();
                for (ItemStack stack : models.keySet())
                    NaturesAura.proxy.registerRenderer(stack, models.get(stack));
            }

            item.onPreInit(event);
        }
    }

    public static void init(FMLInitializationEvent event) {
        for (IModItem item : ALL_ITEMS) {
            if (item instanceof IColorProvidingBlock)
                NaturesAura.proxy.addColorProvidingBlock((IColorProvidingBlock) item);
            if (item instanceof IColorProvidingItem)
                NaturesAura.proxy.addColorProvidingItem((IColorProvidingItem) item);
            if (item instanceof ITESRProvider)
                NaturesAura.proxy.registerTESR((ITESRProvider) item);

            item.onInit(event);
        }
    }

    public static void postInit(FMLPostInitializationEvent event) {
        for (IModItem item : ALL_ITEMS) {
            item.onPostInit(event);
        }
    }
}
