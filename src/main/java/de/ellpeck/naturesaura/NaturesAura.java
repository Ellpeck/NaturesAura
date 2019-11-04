package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.chunk.effect.DrainSpotEffects;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.entities.ModEntities;
import de.ellpeck.naturesaura.events.CommonEvents;
import de.ellpeck.naturesaura.gui.GuiHandler;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.potion.ModPotions;
import de.ellpeck.naturesaura.proxy.ClientProxy;
import de.ellpeck.naturesaura.proxy.IProxy;
import de.ellpeck.naturesaura.proxy.ServerProxy;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(NaturesAura.MOD_ID)
public final class NaturesAura {

    public static final String MOD_ID = NaturesAuraAPI.MOD_ID;
    public static final String MOD_ID_UPPER = MOD_ID.toUpperCase(Locale.ROOT);
    public static final String MOD_NAME = "Nature's Aura";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static NaturesAura instance;

    public NaturesAura() {
        instance = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
    }

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final ItemGroup CREATIVE_TAB = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.GOLD_LEAF);
        }
    };

    public static ResourceLocation createRes(String resource) {
        return new ResourceLocation(MOD_ID, resource);
    }

    public void setup(FMLCommonSetupEvent event) {
        preInit(event);
        init(event);
        postInit(event);
    }

    public void preInit(FMLCommonSetupEvent event) {
        NaturesAuraAPI.setInstance(new InternalHooks());
        Helper.registerCap(IAuraContainer.class);
        Helper.registerCap(IAuraRecharge.class);
        Helper.registerCap(IAuraChunk.class);
        Helper.registerCap(IWorldData.class);

        new ModBlocks();
        new ModItems();
        new ModPotions();

        Compat.preInit();
        PacketHandler.init();
        ModRegistry.preInit(event);
        ModEntities.init();
        new Multiblocks();

        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        proxy.preInit(event);
    }

    public void init(FMLCommonSetupEvent event) {
        ModConfig.initOrReload(false);
        ModRecipes.init();
        ModRegistry.init(event);
        DrainSpotEffects.init();
        new GuiHandler();

        proxy.init(event);
    }

    public void postInit(FMLCommonSetupEvent event) {
        ModRegistry.postInit(event);
        Compat.postInit();
        proxy.postInit(event);

        if (ModConfig.enabledFeatures.removeDragonBreathContainerItem) {
            // TODO Items.DRAGON_BREATH.setContainerItem(null);
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        // TODO event.registerServerCommand(new CommandAura());
    }

}
