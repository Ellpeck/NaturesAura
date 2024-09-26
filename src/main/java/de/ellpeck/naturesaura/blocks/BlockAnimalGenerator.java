package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class BlockAnimalGenerator extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    public BlockAnimalGenerator() {
        super("animal_generator", BlockEntityAnimalGenerator.class, Properties.of().strength(3F).sound(SoundType.STONE));

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingUpdate(EntityTickEvent.Post event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide || entity.level().getGameTime() % 40 != 0 || !(entity instanceof Animal) || entity instanceof Npc)
            return;
        var data = entity.getPersistentData();
        var timeAlive = data.getInt(NaturesAura.MOD_ID + ":time_alive");
        data.putInt(NaturesAura.MOD_ID + ":time_alive", timeAlive + 40);
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide || !(entity instanceof Animal) || entity instanceof Npc)
            return;
        var pos = entity.blockPosition();
        Helper.getBlockEntitiesInArea(entity.level(), pos, 5, tile -> {
            if (!(tile instanceof BlockEntityAnimalGenerator gen))
                return false;

            var data = entity.getPersistentData();
            data.putBoolean(NaturesAura.MOD_ID + ":no_drops", true);

            if (gen.isBusy())
                return false;

            var child = entity.isBaby();
            var timeMod = child ? 0.5F : 1;
            var amountMod = child ? 0.667F : 1;

            var timeAlive = data.getInt(NaturesAura.MOD_ID + ":time_alive");
            var time = Math.min(Mth.floor((timeAlive - 15000) / 500F * timeMod), 200);
            var amount = Math.min(Mth.floor((timeAlive - 8000) / 2F * amountMod), 25000);
            if (time <= 0 || amount <= 0)
                return false;
            gen.setGenerationValues(time, amount);

            var genPos = gen.getBlockPos();
            PacketHandler.sendToAllAround(entity.level(), pos, 32, new PacketParticles(
                (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), PacketParticles.Type.ANIMAL_GEN_CONSUME,
                child ? 1 : 0,
                (int) (entity.getEyeHeight() * 10F),
                genPos.getX(), genPos.getY(), genPos.getZ()));

            return true;
        });
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        var entity = event.getEntity();
        if (entity.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntityExp(LivingExperienceDropEvent event) {
        var entity = event.getEntity();
        if (entity.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":no_drops"))
            event.setCanceled(true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        return new AABB(pos).inflate(5);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
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
