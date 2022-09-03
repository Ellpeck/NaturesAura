package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemDeathRing extends ItemImpl {

    public ItemDeathRing() {
        super("death_ring", new Properties().stacksTo(1));
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static class Events {

        @SubscribeEvent
        public void onDeath(LivingDeathEvent event) {
            var entity = event.getEntity();
            if (!entity.level.isClientSide && entity instanceof Player) {
                var equipped = Helper.getEquippedItem(s -> s.getItem() == ModItems.DEATH_RING, (Player) entity);
                if (!equipped.isEmpty()) {
                    entity.setHealth(entity.getMaxHealth() / 2);
                    entity.removeAllEffects();
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 1));

                    var data = new CompoundTag();
                    data.putInt("id", entity.getId());
                    PacketHandler.sendToAllAround(entity.level, entity.blockPosition(), 32, new PacketClient(1, data));

                    equipped.shrink(1);
                    event.setCanceled(true);
                }
            }
        }
    }
}
