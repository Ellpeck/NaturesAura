package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityStructureFinder extends EyeOfEnderEntity {

    public static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityStructureFinder.class, DataSerializers.VARINT);

    private double targetX;
    private double targetY;
    private double targetZ;
    private int despawnTimer;
    private boolean shatterOrDrop;

    public EntityStructureFinder(EntityType<? extends EyeOfEnderEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(COLOR, 0);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("color", this.dataManager.get(COLOR));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(COLOR, compound.getInt("color"));
    }

    @Override
    public void moveTowards(BlockPos pos) {
        double d0 = pos.getX();
        int i = pos.getY();
        double d1 = pos.getZ();
        double d2 = d0 - this.getPosX();
        double d3 = d1 - this.getPosZ();
        float f = MathHelper.sqrt(d2 * d2 + d3 * d3);
        if (f > 12.0F) {
            this.targetX = this.getPosX() + d2 / (double) f * 12.0D;
            this.targetZ = this.getPosZ() + d3 / (double) f * 12.0D;
            this.targetY = this.getPosY() + 8.0D;
        } else {
            this.targetX = d0;
            this.targetY = i;
            this.targetZ = d1;
        }

        this.despawnTimer = 0;
        this.shatterOrDrop = this.rand.nextInt(4) > 0;
    }

    @Override
    public void tick() {
        this.baseTick();

        Vector3d vec3d = this.getMotion();
        double d0 = this.getPosX() + vec3d.x;
        double d1 = this.getPosY() + vec3d.y;
        double d2 = this.getPosZ() + vec3d.z;
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (double) (180F / (float) Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(vec3d.y, f) * (double) (180F / (float) Math.PI));
        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
            this.prevRotationPitch -= 360.0F;
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            this.prevRotationPitch += 360.0F;
        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            this.prevRotationYaw -= 360.0F;
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            this.prevRotationYaw += 360.0F;
        this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
        this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
        if (!this.world.isRemote) {
            double d3 = this.targetX - d0;
            double d4 = this.targetZ - d2;
            float f1 = (float) Math.sqrt(d3 * d3 + d4 * d4);
            float f2 = (float) MathHelper.atan2(d4, d3);
            double d5 = MathHelper.lerp(0.0025D, f, f1);
            double d6 = vec3d.y;
            if (f1 < 1.0F) {
                d5 *= 0.8D;
                d6 *= 0.8D;
            }

            int j = this.getPosY() < this.targetY ? 1 : -1;
            vec3d = new Vector3d(Math.cos(f2) * d5, d6 + ((double) j - d6) * (double) 0.015F, Math.sin(f2) * d5);
            this.setMotion(vec3d);
        }

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i)
                this.world.addParticle(ParticleTypes.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
        } else if (this.world.isRemote) {
            NaturesAuraAPI.instance().spawnMagicParticle(d0 - vec3d.x * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, d1 - vec3d.y * 0.25D - 0.5D, d2 - vec3d.z * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, vec3d.x * 0.25F, vec3d.y * 0.25F, vec3d.z * 0.25F, this.dataManager.get(COLOR), 1, 50, 0, false, true);
        }

        if (!this.world.isRemote) {
            this.setPosition(d0, d1, d2);
            ++this.despawnTimer;
            if (this.despawnTimer > 80 && !this.world.isRemote) {
                this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.remove();
                if (this.shatterOrDrop) {
                    this.world.addEntity(new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), this.getItem()));
                } else {
                    PacketHandler.sendToAllAround(this.world, this.getPosition(), 32, new PacketParticles((float) this.getPosX(), (float) this.getPosY(), (float) this.getPosZ(), PacketParticles.Type.STRUCTURE_FINDER, this.getEntityId()));
                }
            }
        } else {
            this.setRawPosition(d0, d1, d2);
        }

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
