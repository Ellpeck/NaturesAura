package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.gui.GuiEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import de.ellpeck.naturesaura.reg.*;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        Compat.setupClient();
        ScreenManager.registerFactory(ModContainers.ENDER_CRATE, GuiEnderCrate::new);
        ScreenManager.registerFactory(ModContainers.ENDER_ACCESS, GuiEnderCrate::new);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        for (PlayerRenderer render : new PlayerRenderer[]{skinMap.get("default"), skinMap.get("slim")})
            render.addLayer(new PlayerLayerTrinkets(render));
        new SupporterFancyHandler();
    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {
        for (IModItem item : ModRegistry.ALL_ITEMS) {
            if (item instanceof ICustomRenderType)
                RenderTypeLookup.setRenderLayer((Block) item, ((ICustomRenderType) item).getRenderType().get());
        }
    }

    @Override
    public void addColorProvidingItem(IColorProvidingItem item) {
        ItemColors colors = Minecraft.getInstance().getItemColors();
        IItemColor color = item.getItemColor();

        if (item instanceof Item) {
            colors.register(color, (Item) item);
        } else if (item instanceof Block) {
            colors.register(color, (Block) item);
        }
    }

    @Override
    public void addColorProvidingBlock(IColorProvidingBlock block) {
        if (block instanceof Block) {
            Minecraft.getInstance().getBlockColors().register(block.getBlockColor(), (Block) block);
        }
    }

    @Override
    public void registerTESR(ITESRProvider provider) {
        Tuple<TileEntityType<TileEntity>, Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super TileEntity>>>> tesr = provider.getTESR();
        ClientRegistry.bindTileEntityRenderer(tesr.getA(), tesr.getB().get());
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        ParticleHandler.spawnParticle(() -> new ParticleMagic(Minecraft.getInstance().world,
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
    public void setParticleCulling(boolean cull) {
        ParticleHandler.culling = cull;
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityClass, Supplier<IRenderFactory<T>> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory.get());
    }

}