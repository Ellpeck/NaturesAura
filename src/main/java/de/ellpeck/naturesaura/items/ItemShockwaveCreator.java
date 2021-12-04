package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.items.tools.ItemArmor;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemShockwaveCreator extends ItemImpl implements ITrinketItem {

    public ItemShockwaveCreator() {
        super("shockwave_creator", new Properties().maxStackSize(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (levelIn.isClientSide || !(entityIn instanceof LivingEntity))
            return;
        LivingEntity living = (LivingEntity) entityIn;
        if (!living.isOnGround()) {
            CompoundTag compound = stack.getOrCreateTag();
            if (compound.getBoolean("air"))
                return;

            compound.putBoolean("air", true);
            compound.putDouble("x", living.getPosX());
            compound.putDouble("y", living.getPosY());
            compound.putDouble("z", living.getPosZ());
        } else {
            if (!stack.hasTag())
                return;
            CompoundTag compound = stack.getTag();
            if (!compound.getBoolean("air"))
                return;

            compound.putBoolean("air", false);

            if (!living.isSneaking())
                return;
            if (living.getDistanceSq(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z")) > 0.75F)
                return;
            if (living instanceof Player && !NaturesAuraAPI.instance().extractAuraFromPlayer((Player) living, 1000, false))
                return;

            DamageSource source;
            if (living instanceof Player)
                source = DamageSource.causePlayerDamage((Player) living);
            else
                source = DamageSource.MAGIC;
            boolean infusedSet = ItemArmor.isFullSetEquipped(living, ModArmorMaterial.INFUSED);

            int range = 5;
            List<LivingEntity> mobs = levelIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(
                    living.getPosX() - range, living.getPosY() - 0.5, living.getPosZ() - range,
                    living.getPosX() + range, living.getPosY() + 0.5, living.getPosZ() + range));
            for (LivingEntity mob : mobs) {
                if (!mob.isAlive() || mob == living)
                    continue;
                if (living.getDistanceSq(mob) > range * range)
                    continue;
                if (living instanceof Player && !NaturesAuraAPI.instance().extractAuraFromPlayer((Player) living, 500, false))
                    break;
                mob.attackEntityFrom(source, 4F);

                if (infusedSet)
                    mob.addPotionEffect(new EffectInstance(Effects.WITHER, 120));
            }

            BlockPos pos = living.getPosition();
            BlockPos down = pos.down();
            BlockState downState = levelIn.getBlockState(down);

            if (downState.getMaterial() != Material.AIR) {
                SoundType type = downState.getBlock().getSoundType(downState, levelIn, down, null);
                levelIn.playSound(null, pos, type.getBreakSound(), SoundCategory.BLOCKS, type.getVolume() * 0.5F, type.getPitch() * 0.8F);
            }

            PacketHandler.sendToAllAround(levelIn, pos, 32, new PacketParticles((float) living.getPosX(), (float) living.getPosY(), (float) living.getPosZ(), PacketParticles.Type.SHOCKWAVE_CREATOR));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, Player player, RenderType type, MatrixStack matrices, IRenderTypeBuffer buffer, int packedLight, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
            matrices.translate(0, 0.125F, armor ? -0.195F : -0.1475F);
            matrices.scale(0.3F, 0.3F, 0.3F);
            matrices.rotate(Vector3f.XP.rotationDegrees(180));
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrices, buffer);
        }
    }
}
