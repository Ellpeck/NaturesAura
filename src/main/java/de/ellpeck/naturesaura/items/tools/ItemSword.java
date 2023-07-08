package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ItemSword extends SwordItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemSword(String baseName, Tier material, int damage, float speed) {
        super(material, damage, speed, new Properties());
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this == ModItems.INFUSED_IRON_SWORD) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
        } else if (this == ModItems.SKY_SWORD) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 2));
        } else if (this == ModItems.DEPTH_SWORD && attacker instanceof Player player) {
            // this is just a modified copy of Player.attack's sweeping damage code
            var damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75F;
            for (var other : player.level().getEntitiesOfClass(LivingEntity.class, stack.getSweepHitBox(player, target))) {
                // TODO we removed canHit here, is that okay?
                if (other != player && other != target && !player.isAlliedTo(other) && (!(other instanceof ArmorStand stand) || !stand.isMarker())) {
                    other.knockback(0.4F, Mth.sin(player.getYRot() * (Mth.PI / 180)), -Mth.cos(player.getYRot() * (Mth.PI / 180)));
                    other.hurt(other.damageSources().playerAttack(player), damage);
                }
            }
            // this just displays the particles
            player.sweepAttack();
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NotNull AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        if (this == ModItems.DEPTH_SWORD)
            return target.getBoundingBox().inflate(2, 1, 2);
        return super.getSweepHitBox(stack, player, target);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return Helper.makeRechargeProvider(stack, true);
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
