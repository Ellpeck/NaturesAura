package de.ellpeck.naturesaura;

import com.google.common.base.Strings;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.chunk.effect.DrainSpotEffects;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.CommonEvents;
import de.ellpeck.naturesaura.proxy.ClientProxy;
import de.ellpeck.naturesaura.proxy.IProxy;
import de.ellpeck.naturesaura.proxy.ServerProxy;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NaturesAura.MOD_ID)
public final class NaturesAura {

    public static final String MOD_ID = NaturesAuraAPI.MOD_ID;
    public static final String MOD_NAME = "Nature's Aura";

    public static final Logger LOGGER = LogManager.getLogger(NaturesAura.MOD_NAME);
    public static NaturesAura instance;
    public static IProxy proxy;

    public NaturesAura(ModContainer container) {
        NaturesAura.instance = this;
        NaturesAura.proxy = FMLEnvironment.dist.isClient() ? new ClientProxy() : new ServerProxy();

        container.getEventBus().addListener(this::setup);

        var builder = new ModConfigSpec.Builder();
        ModConfig.instance = new ModConfig(builder);
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, builder.build());
    }

    public void setup(FMLCommonSetupEvent event) {
        this.preInit(event);
        this.init(event);
        this.postInit(event);
    }

    private void preInit(FMLCommonSetupEvent event) {
        Compat.setup(event);
        new Multiblocks();

        NeoForge.EVENT_BUS.register(new CommonEvents());

        NaturesAura.proxy.preInit(event);
    }

    private void init(FMLCommonSetupEvent event) {
        event.enqueueWork(ModConfig.instance::apply);

        ModRecipes.init();
        DrainSpotEffects.init();

        NaturesAura.proxy.init(event);
    }

    private void postInit(FMLCommonSetupEvent event) {
        NaturesAura.proxy.postInit(event);

        NaturesAura.LOGGER.info("-- Nature's Aura Fake Player Information --");
        NaturesAura.LOGGER.info("Name: [Minecraft]");
        NaturesAura.LOGGER.info("UUID: 41C82C87-7AfB-4024-BA57-13D2C99CAE77");
        NaturesAura.LOGGER.info(Strings.padStart("", 43, '-'));
    }

}
