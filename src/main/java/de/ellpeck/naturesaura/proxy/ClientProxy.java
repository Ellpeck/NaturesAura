package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.compat.Compat;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.gui.GuiEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import de.ellpeck.naturesaura.items.ItemColorChanger;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import de.ellpeck.naturesaura.reg.*;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets;
import de.ellpeck.naturesaura.renderers.SupporterFancyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        Compat.setupClient();
        MenuScreens.register(ModContainers.ENDER_CRATE, GuiEnderCrate::new);
        MenuScreens.register(ModContainers.ENDER_ACCESS, GuiEnderCrate::new);

        ItemProperties.register(ModItems.COLOR_CHANGER, new ResourceLocation(NaturesAura.MOD_ID, "fill_mode"),
                (stack, levelIn, entityIn) -> ItemColorChanger.isFillMode(stack) ? 1F : 0F);
        ItemProperties.register(ModItems.COLOR_CHANGER, new ResourceLocation(NaturesAura.MOD_ID, "has_color"),
                (stack, levelIn, entityIn) -> ItemColorChanger.getStoredColor(stack) != null ? 1F : 0F);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        var skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        for (var render : new EntityRenderer[]{skinMap.get("default"), skinMap.get("slim")}) {
            if (render instanceof PlayerRenderer living)
                living.addLayer(new PlayerLayerTrinkets(living));
        }
        new SupporterFancyHandler();
    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {
        for (IModItem item : ModRegistry.ALL_ITEMS) {
            if (item instanceof ICustomRenderType)
                ItemBlockRenderTypes.setRenderLayer((Block) item, ((ICustomRenderType) item).getRenderType().get());
        }
    }

    @Override
    public void addColorProvidingItem(IColorProvidingItem item) {
        ItemColors colors = Minecraft.getInstance().getItemColors();
        ItemColor color = item.getItemColor();

        if (item instanceof Item) {
            colors.register(color, (Item) item);
        } else if (item instanceof Block) {
            colors.register(color, (Block) item);
        }
    }

    @Override
    public void addColorProvidingBlock(IColorProvidingBlock block) {
        if (block instanceof Block)
            Minecraft.getInstance().getBlockColors().register(block.getBlockColor(), (Block) block);
    }

    @Override
    public void registerTESR(ITESRProvider<?> provider) {
        var tesr = provider.getTESR();
        BlockEntityRenderers.register(tesr.getA(), tesr.getB().get());
    }

    @Override
    public void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        ParticleHandler.spawnParticle(() -> new ParticleMagic(Minecraft.getInstance().level,
                posX, posY, posZ,
                motionX, motionY, motionZ,
                color, scale, maxAge, gravity, collision, fade, ParticleHandler.depthEnabled), posX, posY, posZ);
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
    public <T extends Entity> void registerEntityRenderer(EntityType<T> entityClass, Supplier<EntityRendererProvider<T>> renderFactory) {
        EntityRenderers.register(entityClass, renderFactory.get());
    }

}