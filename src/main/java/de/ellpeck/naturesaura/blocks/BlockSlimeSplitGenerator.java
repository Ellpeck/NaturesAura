package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySlimeSplitGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockSlimeSplitGenerator extends BlockContainerImpl {
    public BlockSlimeSplitGenerator() {
        super("slime_split_generator", TileEntitySlimeSplitGenerator::new, Properties.from(Blocks.SLIME_BLOCK).hardnessAndResistance(2));
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    private static class Events {

        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (!(entity instanceof SlimeEntity) || entity.world.isRemote)
                return;
            SlimeEntity slime = (SlimeEntity) entity;
            int size = slime.getSlimeSize();
            if (size <= 1)
                return;
            Helper.getTileEntitiesInArea(entity.world, entity.getPosition(), 8, tile -> {
                if (!(tile instanceof TileEntitySlimeSplitGenerator))
                    return false;
                TileEntitySlimeSplitGenerator gen = (TileEntitySlimeSplitGenerator) tile;
                if (gen.isBusy())
                    return false;
                gen.startGenerating(slime);
                return true;
            });
        }

    }
}
