package de.ellpeck.naturesaura.potion;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Random;

public class PotionBreathless extends PotionImpl {

    private final Random random = new Random();

    public PotionBreathless() {
        super("breathless", MobEffectCategory.HARMFUL, 0);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        var effect = event.getEntity().getEffect(ModPotions.BREATHLESS);
        if (effect == null)
            return;
        var chance = (effect.getAmplifier() + 1) / 15F;
        if (this.random.nextFloat() <= chance) {
            event.setAmount(event.getAmount() / 4F);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        var mod = 200 >> amplifier;
        return mod > 0 && duration % mod == 0 && this.random.nextBoolean();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), 1F);
        return true;
    }

}
