package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.TerrainGenEvents;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.proxy.IProxy;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = NaturesAura.MOD_ID, name = NaturesAura.MOD_NAME, version = NaturesAura.VERSION)
public final class NaturesAura {

    public static final String MOD_ID = "naturesaura";
    public static final String PROXY_LOCATION = "de.ellpeck." + MOD_ID + ".proxy.";
    public static final String MOD_NAME = "Nature's Aura";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @SidedProxy(modId = MOD_ID, clientSide = PROXY_LOCATION + "ClientProxy", serverSide = PROXY_LOCATION + "ServerProxy")
    public static IProxy proxy;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.EYE);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        new ModBlocks();
        new ModItems();

        Compat.init();
        PacketHandler.init();
        ModRegistry.preInit(event);

        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEvents());

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.init();
        ModRegistry.init(event);

        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModRegistry.postInit(event);
        proxy.postInit(event);
    }
}
