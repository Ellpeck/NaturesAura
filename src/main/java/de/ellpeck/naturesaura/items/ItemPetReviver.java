package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.server.ServerLevel;
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
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide || entity.level.getGameTime() % 20 != 0 || !(entity instanceof TameableEntity))
                return;
            TameableEntity tameable = (TameableEntity) entity;
            if (!tameable.isTamed() || !tameable.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;
            LivingEntity owner = tameable.getOwner();
            if (owner == null || owner.getDistanceSq(tameable) > 5 * 5)
                return;
            if (entity.level.rand.nextFloat() >= 0.65F) {
                ((ServerLevel) entity.level).spawnParticle(ParticleTypes.HEART,
                        entity.getPosX() + entity.level.rand.nextGaussian() * 0.25F,
                        entity.getPosYEye() + entity.level.rand.nextGaussian() * 0.25F,
                        entity.getPosZ() + entity.level.rand.nextGaussian() * 0.25F,
                        entity.level.rand.nextInt(2) + 1, 0, 0, 0, 0);
            }
        }

        // we need to use the event since the item doesn't receive the interaction for tamed pets..
        @SubscribeEvent
        public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            Entity target = event.getTarget();
            if (!(target instanceof TameableEntity) || !((TameableEntity) target).isTamed())
                return;
            if (target.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;
            ItemStack stack = event.getPlayer().getHeldItem(event.getHand());
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
            LivingEntity entity = event.getEntityLiving();
            if (entity.level.isClientSide || !(entity instanceof TameableEntity))
                return;
            TameableEntity tameable = (TameableEntity) entity;
            if (!tameable.isTamed() || !tameable.getPersistentData().getBoolean(NaturesAura.MOD_ID + ":pet_reviver"))
                return;

            // get the overworld, and the overworld's spawn point, by default
            ServerLevel spawnLevel = tameable.level.getServer().func_241755_D_();
            Vector3d spawn = Vector3d.copyCenteredHorizontally(spawnLevel.func_241135_u_());

            // check if the owner is online, and respawn at the bed if they are
            LivingEntity owner = tameable.getOwner();
            if (owner instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) owner;
                // I'm not really sure what this means, but I got it from PlayerList.func_232644_a_ haha
                BlockPos pos = player.func_241140_K_();
                if (pos != null) {
                    float f = player.func_242109_L();
                    boolean b = player.func_241142_M_();
                    Optional<Vector3d> bed = Player.func_242374_a((ServerLevel) player.level, pos, f, b, false);
                    if (bed.isPresent()) {
                        spawnLevel = (ServerLevel) player.level;
                        spawn = bed.get();
                    }
                }
            }

            PacketHandler.sendToAllAround(tameable.level, tameable.getPosition(), 32, new PacketParticles((float) tameable.getPosX(), (float) tameable.getPosYEye(), (float) tameable.getPosZ(), PacketParticles.Type.PET_REVIVER, 0xc2461d));

            TameableEntity spawnedPet = tameable;
            if (tameable.level != spawnLevel) {
                ((ServerLevel) tameable.level).removeEntity(tameable, true);
                spawnedPet = (TameableEntity) tameable.getType().create(spawnLevel);
            }
            // respawn (a copy of) the pet
            spawnedPet.copyDataFromOld(tameable);
            spawnedPet.setMotion(0, 0, 0);
            spawnedPet.setLocationAndAngles(spawn.x, spawn.y, spawn.z, tameable.rotationYaw, tameable.rotationPitch);
            while (!spawnLevel.hasNoCollisions(spawnedPet))
                spawnedPet.setPosition(spawnedPet.getPosX(), spawnedPet.getPosY() + 1, spawnedPet.getPosZ());
            spawnedPet.setHealth(spawnedPet.getMaxHealth());
            spawnedPet.getNavigator().clearPath();
            // sit down (on the server side!)
            spawnedPet.func_233687_w_(true);
            spawnedPet.setJumping(false);
            spawnedPet.setAttackTarget(null);
            if (tameable.level != spawnLevel) {
                spawnLevel.addEntity(spawnedPet);
                tameable.remove(false);
            }

            // drain aura
            BlockPos auraPos = IAuraChunk.getHighestSpot(spawnLevel, spawnedPet.getPosition(), 35, spawnedPet.getPosition());
            IAuraChunk.getAuraChunk(spawnLevel, auraPos).drainAura(auraPos, 200000);

            PacketHandler.sendToAllAround(spawnedPet.level, spawnedPet.getPosition(), 32, new PacketParticles((float) spawnedPet.getPosX(), (float) spawnedPet.getPosYEye(), (float) spawnedPet.getPosZ(), PacketParticles.Type.PET_REVIVER, 0x4dba2f));

            if (owner instanceof Player)
                owner.sendMessage(new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".pet_reviver", spawnedPet.getDisplayName()).mergeStyle(TextFormatting.ITALIC, TextFormatting.GRAY), UUID.randomUUID());
            event.setCanceled(true);
        }
    }
}
