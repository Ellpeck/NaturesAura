package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.packet.PacketClient;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemDeathRing extends ItemImpl {
    public ItemDeathRing() {
        super("death_ring", new Properties().maxStackSize(1));
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static class Events {

        @SubscribeEvent
        public void onDeath(LivingDeathEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (!entity.level.isClientSide && entity instanceof Player) {
                ItemStack equipped = Helper.getEquippedItem(s -> s.getItem() == ModItems.DEATH_RING, (Player) entity);
                if (!equipped.isEmpty()) {
                    entity.setHealth(entity.getMaxHealth() / 2);
                    entity.clearActivePotions();
                    entity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 500, 1));

                    CompoundTag data = new CompoundTag();
                    data.putInt("id", entity.getEntityId());
                    PacketHandler.sendToAllAround(entity.level, entity.getPosition(), 32, new PacketClient(1, data));

                    equipped.shrink(1);
                    event.setCanceled(true);
                }
            }
        }
    }
}
