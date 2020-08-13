package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAnimalGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockAnimalGenerator extends BlockContainerImpl implements IVisualizable, ICustomBlockState {
    public BlockAnimalGenerator() {
        super("animal_generator", TileEntityAnimalGenerator::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(3F).sound(SoundType.STONE));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.world.isRemote || entity.world.getGameTime() % 40 != 0 || !(entity instanceof AnimalEntity) || entity instanceof IMob || entity instanceof INPC)
            return;
        CompoundNBT data = entity.getPersistentData();
        int timeAlive = data.getInt(NaturesAura.MOD_ID + ":time_alive");
        data.putInt(NaturesAura.MOD_ID + ":time_alive", timeAlive + 40);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.world.isRemote || !(entity instanceof AnimalEntity) || entity instanceof IMob || entity instanceof INPC)
            return;
        BlockPos pos = entity.getPosition();
        Helper.getTileEntitiesInArea(entity.world, pos, 5, tile -> {
            if (!(tile instanceof TileEntityAnimalGenerator))
                return false;
            TileEntityAnimalGenerator gen = (TileEntityAnimalGenerator) tile;

            CompoundNBT data = entity.getPersistentData();
            data.putBoolean(NaturesAura.MOD_ID + ":no_drops", true);

            if (gen.isBusy())
                return false;

            boolean child = entity.isChild();
            float timeMod = child ? 0.5F : 1;
            float amountMod = child ? 0.667F : 1;

            int timeAlive = data.getInt(NaturesAura.MOD_ID + ":time_alive");
            int time = Math.min(MathHelper.floor((timeAlive - 15000) / 500F * timeMod), 200);
            int amount = Math.min(MathHelper.floor((timeAlive - 8000) / 2F * amountMod), 25000);
            if (time <= 0 || amount <= 0)
                return false;
            gen.setGenerationValues(time, amount);

            BlockPos genPos = gen.getPos();
            PacketHandler.sendToAllAround(entity.world, pos, 32, new PacketParticles(
                    (float) entity.getPosX(), (float) entity.getPosY(), (float) entity.getPosZ(), PacketParticles.Type.ANIMAL_GEN_CONSUME,
                    child ? 1 : 0,
                    (int) (entity.getEyeHeight() * 10F),
                    genPos.getX(), genPos.getY(), genPos.getZ()));

            return true;
        });
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityExp(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(5);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x11377a;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}