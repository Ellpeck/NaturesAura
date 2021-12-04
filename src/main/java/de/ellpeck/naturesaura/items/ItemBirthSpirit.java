package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemBirthSpirit extends ItemGlowing {
    public ItemBirthSpirit() {
        super("birth_spirit");
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    private static class EventHandler {
        @SubscribeEvent
        public void onBabyBorn(BabyEntitySpawnEvent event) {
            LivingEntity parent = event.getParentA();
            if (!parent.level.isClientSide && event.getCausedByPlayer() != null) {
                BlockPos pos = parent.getPosition();
                int aura = IAuraChunk.getAuraInArea(parent.level, pos, 30);
                if (aura < 1200000)
                    return;

                int amount = parent.level.rand.nextInt(3) + 1;
                ItemEntity item = new ItemEntity(parent.level, parent.getPosX(), parent.getPosY(), parent.getPosZ(),
                        new ItemStack(ModItems.BIRTH_SPIRIT, amount));
                parent.level.addEntity(item);

                BlockPos spot = IAuraChunk.getHighestSpot(parent.level, pos, 30, pos);
                IAuraChunk.getAuraChunk(parent.level, spot).drainAura(spot, 800 * amount);
            }
        }
    }
}
