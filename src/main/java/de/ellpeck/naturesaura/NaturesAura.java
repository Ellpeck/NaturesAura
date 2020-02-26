package de.ellpeck.naturesaura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.chunk.effect.DrainSpotEffects;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.CommonEvents;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.proxy.ClientProxy;
import de.ellpeck.naturesaura.proxy.IProxy;
import de.ellpeck.naturesaura.proxy.ServerProxy;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NaturesAura.MOD_ID)
public final class NaturesAura {

    public static final String MOD_ID = NaturesAuraAPI.MOD_ID;
    public static final String MOD_NAME = "Nature's Aura";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final ItemGroup CREATIVE_TAB = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.GOLD_LEAF);
        }
    };
    public static NaturesAura instance;
    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public NaturesAura() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        ModConfig.instance = new ModConfig(builder);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, builder.build());
    }

    public void setup(FMLCommonSetupEvent event) {
        this.preInit(event);
        this.init(event);
        this.postInit(event);
    }

    private void preInit(FMLCommonSetupEvent event) {
        NaturesAuraAPI.setInstance(new InternalHooks());
        Helper.registerCap(IAuraContainer.class);
        Helper.registerCap(IAuraRecharge.class);
        Helper.registerCap(IAuraChunk.class);
        Helper.registerCap(IWorldData.class);

        Compat.preInit();
        PacketHandler.init();
        new Multiblocks();

        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        proxy.preInit(event);
    }

    private void init(FMLCommonSetupEvent event) {
        ModConfig.instance.apply();

        ModRecipes.init();
        ModRegistry.init();
        DrainSpotEffects.init();

        proxy.init(event);
    }

    private void postInit(FMLCommonSetupEvent event) {
        Compat.postInit();
        proxy.postInit(event);
    }

}
