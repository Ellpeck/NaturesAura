package de.ellpeck.naturesaura.proxy;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IProxy {

    void preInit(FMLCommonSetupEvent event);

    void init(FMLCommonSetupEvent event);

    void postInit(FMLCommonSetupEvent event);

    void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade);

    void setParticleDepth(boolean depth);

    void setParticleSpawnRange(int range);

    void setParticleCulling(boolean cull);
}
