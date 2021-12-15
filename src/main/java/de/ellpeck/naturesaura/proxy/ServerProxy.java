package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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
    public void addColorProvidingItem(IColorProvidingItem item) {

    }

    @Override
    public void addColorProvidingBlock(IColorProvidingBlock block) {

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
    public void setParticleCulling(boolean cull) {

    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityClass, Supplier<EntityRendererProvider<T>> renderFactory) {

    }
}