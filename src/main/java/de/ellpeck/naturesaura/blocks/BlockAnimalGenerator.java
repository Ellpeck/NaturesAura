package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAnimalGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockAnimalGenerator extends BlockContainerImpl {
    public BlockAnimalGenerator() {
        super(Material.ROCK, "animal_generator", TileEntityAnimalGenerator.class, "animal_generator");
        this.setSoundType(SoundType.STONE);
        this.setHardness(3F);

        MinecraftForge.EVENT_BUS.register(this);
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
            entity.getEntityData().setBoolean(NaturesAura.MOD_ID + ":no_drops", true);

            if (gen.isBusy())
                return false;

            boolean child = entity.isChild();
            int time = child ? 60 : 120;
            int amount = child ? 40 : 60;
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
}