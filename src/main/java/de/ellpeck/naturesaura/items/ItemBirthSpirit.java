package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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
            if (!parent.level().isClientSide && event.getCausedByPlayer() != null) {
                var pos = parent.blockPosition();
                var aura = IAuraChunk.getAuraInArea(parent.level(), pos, 30);
                if (aura < 1200000)
                    return;

                var amount = parent.level().random.nextInt(3) + 1;
                var item = new ItemEntity(parent.level(), parent.getX(), parent.getY(), parent.getZ(), new ItemStack(ModItems.BIRTH_SPIRIT, amount));
                parent.level().addFreshEntity(item);

                var spot = IAuraChunk.getHighestSpot(parent.level(), pos, 30, pos);
                IAuraChunk.getAuraChunk(parent.level(), spot).drainAura(spot, 800 * amount);
            }
        }
    }
}
