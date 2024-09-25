package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityStructureFinder extends EyeOfEnder {

    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityStructureFinder.class, EntityDataSerializers.INT);

    private double targetX;
    private double targetY;
    private double targetZ;
    private int despawnTimer;
    private boolean shatterOrDrop;

    public EntityStructureFinder(EntityType<? extends EyeOfEnder> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EntityStructureFinder.COLOR, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("color", this.entityData.get(EntityStructureFinder.COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(EntityStructureFinder.COLOR, compound.getInt("color"));
    }

    @Override
    public void signalTo(BlockPos pos) {
        double d0 = pos.getX();
        var i = pos.getY();
        double d1 = pos.getZ();
        var d2 = d0 - this.getX();
        var d3 = d1 - this.getZ();
        var f = Math.sqrt(d2 * d2 + d3 * d3);
        if (f > 12.0F) {
            this.targetX = this.getX() + d2 / f * 12.0D;
            this.targetZ = this.getZ() + d3 / f * 12.0D;
            this.targetY = this.getY() + 8.0D;
        } else {
            this.targetX = d0;
            this.targetY = i;
            this.targetZ = d1;
        }

        this.despawnTimer = 0;
        this.shatterOrDrop = this.random.nextInt(4) > 0;
    }

    @Override
    public void tick() {
        this.baseTick();

        var vec3d = this.getDeltaMovement();
        var d0 = this.getX() + vec3d.x;
        var d1 = this.getY() + vec3d.y;
        var d2 = this.getZ() + vec3d.z;
        var f = Math.sqrt(vec3d.horizontalDistance());
        this.setYRot((float) (Mth.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3d.y, f) * (double) (180F / (float) Math.PI)));
        while (this.getXRot() - this.xRotO < -180.0F)
            this.xRotO -= 360.0F;
        while (this.getXRot() - this.xRotO >= 180.0F)
            this.xRotO += 360.0F;
        while (this.getYRot() - this.yRotO < -180.0F)
            this.yRotO -= 360.0F;
        while (this.getYRot() - this.yRotO >= 180.0F)
            this.yRotO += 360.0F;
        this.setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));
        this.setYRot(Mth.lerp(0.2F, this.yRotO, this.getYRot()));
        if (!this.level().isClientSide) {
            var d3 = this.targetX - d0;
            var d4 = this.targetZ - d2;
            var f1 = (float) Math.sqrt(d3 * d3 + d4 * d4);
            var f2 = (float) Mth.atan2(d4, d3);
            var d5 = Mth.lerp(0.0025D, f, f1);
            var d6 = vec3d.y;
            if (f1 < 1.0F) {
                d5 *= 0.8D;
                d6 *= 0.8D;
            }

            var j = this.getY() < this.targetY ? 1 : -1;
            vec3d = new Vec3(Math.cos(f2) * d5, d6 + ((double) j - d6) * (double) 0.015F, Math.sin(f2) * d5);
            this.setDeltaMovement(vec3d);
        }

        if (this.isInWater()) {
            for (var i = 0; i < 4; ++i)
                this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
        } else if (this.level().isClientSide) {
            NaturesAuraAPI.instance().spawnMagicParticle(d0 - vec3d.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3d.y * 0.25D - 0.5D, d2 - vec3d.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3d.x * 0.25F, vec3d.y * 0.25F, vec3d.z * 0.25F, this.entityData.get(EntityStructureFinder.COLOR), 1, 50, 0, false, true);
        }

        if (!this.level().isClientSide) {
            this.setPos(d0, d1, d2);
            ++this.despawnTimer;
            if (this.despawnTimer > 80 && !this.level().isClientSide) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.remove(RemovalReason.DISCARDED);
                if (this.shatterOrDrop) {
                    this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
                } else {
                    PacketHandler.sendToAllAround(this.level(), this.blockPosition(), 32, new PacketParticles((float) this.getX(), (float) this.getY(), (float) this.getZ(), PacketParticles.Type.STRUCTURE_FINDER, this.getId()));
                }
            }
        } else {
            this.setPosRaw(d0, d1, d2);
        }

    }

}
