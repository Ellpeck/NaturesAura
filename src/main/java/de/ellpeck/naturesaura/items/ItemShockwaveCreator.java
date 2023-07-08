package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.items.tools.ItemArmor;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemShockwaveCreator extends ItemImpl implements ITrinketItem {

    public ItemShockwaveCreator() {
        super("shockwave_creator", new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (levelIn.isClientSide || !(entityIn instanceof LivingEntity living))
            return;
        if (!living.onGround()) {
            var compound = stack.getOrCreateTag();
            if (compound.getBoolean("air"))
                return;

            compound.putBoolean("air", true);
            compound.putDouble("x", living.getX());
            compound.putDouble("y", living.getY());
            compound.putDouble("z", living.getZ());
        } else {
            if (!stack.hasTag())
                return;
            var compound = stack.getTag();
            if (!compound.getBoolean("air"))
                return;

            compound.putBoolean("air", false);

            if (!living.isShiftKeyDown())
                return;
            if (living.distanceToSqr(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z")) > 0.75F)
                return;
            if (living instanceof Player && !NaturesAuraAPI.instance().extractAuraFromPlayer((Player) living, 1000, false))
                return;

            var infusedSet = ItemArmor.isFullSetEquipped(living, ModArmorMaterial.INFUSED);
            var range = 5;
            var mobs = levelIn.getEntitiesOfClass(LivingEntity.class, new AABB(
                    living.getX() - range, living.getY() - 0.5, living.getZ() - range,
                    living.getX() + range, living.getY() + 0.5, living.getZ() + range));
            for (var mob : mobs) {
                if (!mob.isAlive() || mob == living)
                    continue;
                if (living.distanceToSqr(mob) > range * range)
                    continue;
                if (living instanceof Player && !NaturesAuraAPI.instance().extractAuraFromPlayer((Player) living, 500, false))
                    break;

                DamageSource source;
                if (living instanceof Player)
                    source = mob.damageSources().playerAttack((Player) living);
                else
                    source = mob.damageSources().magic();
                mob.hurt(source, 4F);

                if (infusedSet)
                    mob.addEffect(new MobEffectInstance(MobEffects.WITHER, 120));
            }

            var pos = living.blockPosition();
            var down = pos.below();
            var downState = levelIn.getBlockState(down);

            if (!downState.isAir()) {
                var type = downState.getBlock().getSoundType(downState, levelIn, down, null);
                levelIn.playSound(null, pos, type.getBreakSound(), SoundSource.BLOCKS, type.getVolume() * 0.5F, type.getPitch() * 0.8F);
            }

            PacketHandler.sendToAllAround(levelIn, pos, 32, new PacketParticles((float) living.getX(), (float) living.getY(), (float) living.getZ(), PacketParticles.Type.SHOCKWAVE_CREATOR));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, Player player, RenderType type, PoseStack matrices, MultiBufferSource buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            var armor = !player.getInventory().armor.get(EquipmentSlot.CHEST.getIndex()).isEmpty();
            matrices.translate(0, 0.125F, armor ? -0.195F : -0.1475F);
            matrices.scale(0.3F, 0.3F, 0.3F);
            matrices.mulPose(Axis.XP.rotationDegrees(180));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer, player.level(), 0);
        }
    }
}
