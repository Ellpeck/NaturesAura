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
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        Compat.preInitClient();
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
        Tuple<Class, TileEntityRenderer> tesr = provider.getTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(tesr.getA(), tesr.getB());
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
}