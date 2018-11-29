package de.ellpeck.naturesaura.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class PotionBreathless extends PotionImpl {

    private final Random random = new Random();

    protected PotionBreathless() {
        super("breathless", true, 0);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        PotionEffect effect = event.getEntityLiving().getActivePotionEffect(this);
        if (effect == null)
            return;
        float chance = (effect.getAmplifier() + 1) / 15F;
        if (this.random.nextFloat() <= chance) {
            event.setAmount(event.getAmount() / 4F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int mod = 200 >> amplifier;
        return mod > 0 && duration % mod == 0 && this.random.nextBoolean();
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        entity.attackEntityFrom(DamageSource.MAGIC, 1F);
    }
}
