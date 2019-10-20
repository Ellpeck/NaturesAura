package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        Compat.preInitClient();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Map<String, PlayerRenderer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        for (PlayerRenderer render : new PlayerRenderer[]{skinMap.get("default"), skinMap.get("slim")}) {
            render.addLayer(new PlayerLayerTrinkets());
        }
        new SupporterFancyHandler();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void registerRenderer(ItemStack stack, ModelResourceLocation location) {
        ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getItemDamage(), location);
    }

    @Override
    public void addColorProvidingItem(IColorProvidingItem item) {
        ItemColors colors = Minecraft.getMinecraft().getItemColors();
        IItemColor color = item.getItemColor();

        if (item instanceof Item) {
            colors.registerItemColorHandler(color, (Item) item);
        } else if (item instanceof Block) {
            colors.registerItemColorHandler(color, (Block) item);
        }
    }

    @Override
    public void addColorProvidingBlock(IColorProvidingBlock block) {
        if (block instanceof Block) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(block.getBlockColor(), (Block) block);
        }
    }

    @Override
    public void registerTESR(ITESRProvider provider) {
        Tuple<Class, TileEntityRenderer> tesr = provider.getTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(tesr.getFirst(), tesr.getSecond());
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(Class<T> entityClass, Supplier<IRenderFactory<T>> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory.get());
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        ParticleHandler.spawnParticle(() -> new ParticleMagic(Minecraft.getMinecraft().world,
                posX, posY, posZ,
                motionX, motionY, motionZ,
                color, scale, maxAge, gravity, collision, fade), posX, posY, posZ);
    }

    @Override
    public void setParticleDepth(boolean depth) {
        ParticleHandler.depthEnabled = depth;
    }

    @Override
    public void setParticleSpawnRange(int range) {
        ParticleHandler.range = range;
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}