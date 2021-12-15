package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;
import java.util.UUID;

public class ItemPetReviver extends ItemImpl {

    public ItemPetReviver() {
        super("pet_reviver");
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    private static class Events {

        @SubscribeEvent
        public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            var entity = event.getEntityLiving();
            if (entity.level.isClientSide || entity.level.getGameTime() % 20 != 0 || !(entity instanceof TamableAnimal tameable))
                return;
            if (!tameable.isTame() || !tameable.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;
            var owner = tameable.getOwner();
            if (owner == null || owner.distanceToSqr(tameable) > 5 * 5)
                return;
            if (entity.level.random.nextFloat() >= 0.65F) {
                ((ServerLevel) entity.level).sendParticles(ParticleTypes.HEART,
                        entity.getX() + entity.level.random.nextGaussian() * 0.25F,
                        entity.getEyeY() + entity.level.random.nextGaussian() * 0.25F,
                        entity.getZ() + entity.level.random.nextGaussian() * 0.25F,
                        entity.level.random.nextInt(2) + 1, 0, 0, 0, 0);
            }
        }

        // we need to use the event since the item doesn't receive the interaction for tamed pets...
        @SubscribeEvent
        public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            var target = event.getTarget();
            if (!(target instanceof TamableAnimal) || !((TamableAnimal) target).isTame())
                return;
            if (target.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;
            var stack = event.getPlayer().getItemInHand(event.getHand());
            if (stack.getItem() != ModItems.PET_REVIVER)
                return;
            target.getPersistentData().putBoolean(NaturesAura.MOD_ID + ":pet_reviver", true);
            if (!target.level.isClientSide)
                stack.shrink(1);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }

        // we want to be sure that the pet is really dying, so we want to receive the event last
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onLivingDeath(LivingDeathEvent event) {
            var entity = event.getEntityLiving();
            if (entity.level.isClientSide || !(entity instanceof TamableAnimal tameable))
                return;
            if (!tameable.isTame() || !tameable.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;

            // get the overworld, and the overworld's spawn point, by default
            var spawnLevel = tameable.level.getServer().overworld();
            var spawn = Vec3.atBottomCenterOf(spawnLevel.getSharedSpawnPos());

            // check if the owner is online, and respawn at the bed if they are
            var owner = tameable.getOwner();
            if (owner instanceof ServerPlayer player) {
                var pos = player.getRespawnPosition();
                if (pos != null) {
                    var f = player.getRespawnAngle();
                    var b = player.isRespawnForced();
                    var bed = Player.findRespawnPositionAndUseSpawnBlock((ServerLevel) player.level, pos, f, b, false);
                    if (bed.isPresent()) {
                        spawnLevel = (ServerLevel) player.level;
                        spawn = bed.get();
                    }
                }
            }

            PacketHandler.sendToAllAround(tameable.level, tameable.blockPosition(), 32, new PacketParticles((float) tameable.getX(), (float) tameable.getEyeY(), (float) tameable.getZ(), PacketParticles.Type.PET_REVIVER, 0xc2461d));

            var spawnedPet = tameable;
            if (tameable.level != spawnLevel) {
                ((ServerLevel) tameable.level).removeEntity(tameable, true);
                spawnedPet = (TamableAnimal) tameable.getType().create(spawnLevel);
            }
            // respawn (a copy of) the pet
            spawnedPet.restoreFrom(tameable);
            spawnedPet.setDeltaMovement(0, 0, 0);
            spawnedPet.moveTo(spawn.x, spawn.y, spawn.z, tameable.getYRot(), tameable.getXRot());
            while (!spawnLevel.noCollision(spawnedPet))
                spawnedPet.setPos(spawnedPet.getX(), spawnedPet.getY() + 1, spawnedPet.getZ());
            spawnedPet.setHealth(spawnedPet.getMaxHealth());
            spawnedPet.getNavigation().stop();
            // sit down (on the server side!)
            spawnedPet.setInSittingPose(true);
            spawnedPet.setJumping(false);
            spawnedPet.setTarget(null);
            if (tameable.level != spawnLevel) {
                spawnLevel.addFreshEntity(spawnedPet);
                tameable.remove(Entity.RemovalReason.DISCARDED);
            }

            // drain aura
            var auraPos = IAuraChunk.getHighestSpot(spawnLevel, spawnedPet.blockPosition(), 35, spawnedPet.blockPosition());
            IAuraChunk.getAuraChunk(spawnLevel, auraPos).drainAura(auraPos, 200000);

            PacketHandler.sendToAllAround(spawnedPet.level, spawnedPet.blockPosition(), 32, new PacketParticles((float) spawnedPet.getX(), (float) spawnedPet.getEyeY(), (float) spawnedPet.getZ(), PacketParticles.Type.PET_REVIVER, 0x4dba2f));

            if (owner instanceof Player)
                owner.sendMessage(new TranslatableComponent("info." + NaturesAura.MOD_ID + ".pet_reviver", spawnedPet.getDisplayName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY), UUID.randomUUID());
            event.setCanceled(true);
        }
    }
}
