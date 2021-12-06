package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

public interface IProxy {

    void preInit(FMLCommonSetupEvent event);

    void init(FMLCommonSetupEvent event);

    void postInit(FMLCommonSetupEvent event);

    void addColorProvidingItem(IColorProvidingItem item);

    void addColorProvidingBlock(IColorProvidingBlock block);

    void registerTESR(ITESRProvider<?> provider);

    void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade);

    void setParticleDepth(boolean depth);

    void setParticleSpawnRange(int range);

    void setParticleCulling(boolean cull);

    <T extends Entity> void registerEntityRenderer(EntityType<T> entityClass, Supplier<EntityRendererProvider<T>> renderFactory);
}
