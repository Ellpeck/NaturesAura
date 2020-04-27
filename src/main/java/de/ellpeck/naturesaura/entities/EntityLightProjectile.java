package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityLightProjectile extends ThrowableEntity {
    public EntityLightProjectile(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityLightProjectile(EntityType<? extends ThrowableEntity> type, LivingEntity livingEntityIn, World worldIn) {
        super(type, livingEntityIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote && this.ticksExisted > 1) {
            for (float i = 0; i <= 1; i += 0.2F) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        MathHelper.lerp(i, this.prevPosX, this.getPosX()),
                        MathHelper.lerp(i, this.prevPosY, this.getPosY()),
                        MathHelper.lerp(i, this.prevPosZ, this.getPosZ()),
                        this.rand.nextGaussian() * 0.01F, this.rand.nextGaussian() * 0.01F, this.rand.nextGaussian() * 0.01F,
                        0xffcb5c, this.rand.nextFloat() * 0.5F + 1, 20, 0, false, true);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result instanceof BlockRayTraceResult) {
                BlockRayTraceResult res = (BlockRayTraceResult) result;
                BlockPos pos = res.getPos().offset(res.getFace());
                BlockState state = this.world.getBlockState(pos);
                if (state.getMaterial().isReplaceable())
                    this.world.setBlockState(pos, ModBlocks.LIGHT.getDefaultState());
            } else if (result instanceof EntityRayTraceResult) {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                entity.setFire(5);
            }
        }
        this.remove();
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
