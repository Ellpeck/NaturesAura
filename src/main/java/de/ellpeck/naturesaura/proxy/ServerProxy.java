package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class ServerProxy implements IProxy {

    @Override
    public void preInit(FMLCommonSetupEvent event) {

    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {

    }

    @Override
    public void registerRenderer(ItemStack stack, ModelResourceLocation location) {

    }

    @Override
    public void addColorProvidingItem(IColorProvidingItem item) {

    }

    @Override
    public void addColorProvidingBlock(IColorProvidingBlock block) {

    }

    @Override
    public void registerTESR(ITESRProvider provider) {

    }

    @Override
    public <T extends Entity> void registerEntityRenderer(Class<T> entityClass, Supplier<IRenderFactory<T>> renderFactory) {

    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {

    }

    @Override
    public void setParticleDepth(boolean depth) {

    }

    @Override
    public void setParticleSpawnRange(int range) {

    }

    @Override
    public void scheduleTask(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().runAsync(runnable);
    }
}