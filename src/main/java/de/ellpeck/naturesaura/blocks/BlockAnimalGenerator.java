package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAnimalGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAnimalGenerator extends BlockContainerImpl implements IVisualizable {
    public BlockAnimalGenerator() {
        super(Material.ROCK, "animal_generator", TileEntityAnimalGenerator.class, "animal_generator");
        this.setSoundType(SoundType.STONE);
        this.setHardness(3F);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof IAnimals) || entity instanceof IMob || entity instanceof INpc)
            return;
        NBTTagCompound data = entity.getEntityData();
        int timeAlive = data.getInteger(NaturesAura.MOD_ID + ":time_alive");
        data.setInteger(NaturesAura.MOD_ID + ":time_alive", timeAlive + 1);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof IAnimals) || entity instanceof IMob || entity instanceof INpc)
            return;
        BlockPos pos = entity.getPosition();
        Helper.getTileEntitiesInArea(entity.world, pos, 5, tile -> {
            if (!(tile instanceof TileEntityAnimalGenerator))
                return false;
            TileEntityAnimalGenerator gen = (TileEntityAnimalGenerator) tile;

            NBTTagCompound data = entity.getEntityData();
            data.setBoolean(NaturesAura.MOD_ID + ":no_drops", true);

            if (gen.isBusy())
                return false;

            boolean child = entity.isChild();
            float timeMod = child ? 0.5F : 1;
            float amountMod = child ? 0.667F : 1;

            int timeAlive = data.getInteger(NaturesAura.MOD_ID + ":time_alive");
            int time = Math.min(MathHelper.floor((timeAlive - 15000) / 500F * timeMod), 300);
            int amount = Math.min(MathHelper.floor((timeAlive - 8000) / 2.5F * amountMod), 15000);
            if (time <= 0 || amount <= 0)
                return false;
            gen.setGenerationValues(time, amount);

            BlockPos genPos = gen.getPos();
            PacketHandler.sendToAllAround(entity.world, pos, 32, new PacketParticles(
                    (float) entity.posX, (float) entity.posY, (float) entity.posZ, 17,
                    child ? 1 : 0,
                    (int) (entity.getEyeHeight() * 10F),
                    genPos.getX(), genPos.getY(), genPos.getZ()));
            return true;
        });
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.getEntityData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityExp(LivingExperienceDropEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.getEntityData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x11377a;
    }
}