package de.ellpeck.naturesaura.entities;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EntityLightProjectile extends ThrowableProjectile {

    public EntityLightProjectile(EntityType<? extends ThrowableProjectile> type, Level levelIn) {
        super(type, levelIn);
    }

    public EntityLightProjectile(EntityType<? extends ThrowableProjectile> type, LivingEntity livingEntityIn, Level levelIn) {
        super(type, livingEntityIn, levelIn);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.tickCount > 1) {
            for (float i = 0; i <= 1; i += 0.2F) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        Mth.lerp(i, this.xOld, this.getX()),
                        Mth.lerp(i, this.yOld, this.getY()),
                        Mth.lerp(i, this.zOld, this.getZ()),
                        this.random.nextGaussian() * 0.01F, this.random.nextGaussian() * 0.01F, this.random.nextGaussian() * 0.01F,
                        0xffcb5c, this.random.nextFloat() * 0.5F + 1, 20, 0, false, true);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            if (result instanceof BlockHitResult res) {
                var pos = res.getBlockPos().relative(res.getDirection());
                var state = this.level().getBlockState(pos);
                if (state.canBeReplaced())
                    this.level().setBlockAndUpdate(pos, ModBlocks.LIGHT.defaultBlockState());
            } else if (result instanceof EntityHitResult entity) {
                entity.getEntity().setSecondsOnFire(5);
            }
        }
        this.discard();
    }

}
