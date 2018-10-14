package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.particles.ParticleHandler;
import de.ellpeck.naturesaura.particles.ParticleMagic;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void registerRenderer(ItemStack stack, ResourceLocation location, String variant) {
        ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getItemDamage(), new ModelResourceLocation(location, variant));
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
    public void spawnMagicParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        ParticleMagic particle = new ParticleMagic(world, posX, posY, posZ, motionX, motionY, motionZ, color, scale, maxAge, gravity, collision, fade);
        ParticleHandler.spawnParticle(particle, posX, posY, posZ, 32);
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}