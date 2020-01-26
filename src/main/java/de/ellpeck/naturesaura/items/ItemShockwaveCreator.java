package de.ellpeck.naturesaura.items;

import com.mojang.blaze3d.platform.GlStateManager;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.ITrinketItem;
import de.ellpeck.naturesaura.items.tools.ItemArmor;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemShockwaveCreator extends ItemImpl implements ITrinketItem {

    private static final ResourceLocation RES_WORN = new ResourceLocation(NaturesAura.MOD_ID, "textures/items/shockwave_creator_player.png");

    public ItemShockwaveCreator() {
        super("shockwave_creator", new Properties().maxStackSize(1).group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || !(entityIn instanceof LivingEntity))
            return;
        LivingEntity living = (LivingEntity) entityIn;
        if (!living.onGround) {
            CompoundNBT compound = stack.getOrCreateTag();
            if (compound.getBoolean("air"))
                return;

            compound.putBoolean("air", true);
            compound.putDouble("x", living.posX);
            compound.putDouble("y", living.posY);
            compound.putDouble("z", living.posZ);
        } else {
            if (!stack.hasTag())
                return;
            CompoundNBT compound = stack.getTag();
            if (!compound.getBoolean("air"))
                return;

            compound.putBoolean("air", false);

            if (!living.isSneaking())
                return;
            if (living.getDistanceSq(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z")) > 0.75F)
                return;
            if (living instanceof PlayerEntity && !NaturesAuraAPI.instance().extractAuraFromPlayer((PlayerEntity) living, 1000, false))
                return;

            DamageSource source;
            if (living instanceof PlayerEntity)
                source = DamageSource.causePlayerDamage((PlayerEntity) living);
            else
                source = DamageSource.MAGIC;
            boolean infusedSet = ItemArmor.isFullSetEquipped(living, 0);

            int range = 5;
            List<LivingEntity> mobs = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(
                    living.posX - range, living.posY - 0.5, living.posZ - range,
                    living.posX + range, living.posY + 0.5, living.posZ + range));
            for (LivingEntity mob : mobs) {
                if (!mob.isAlive() || mob == living)
                    continue;
                if (living.getDistanceSq(mob) > range * range)
                    continue;
                if (living instanceof PlayerEntity && !NaturesAuraAPI.instance().extractAuraFromPlayer((PlayerEntity) living, 500, false))
                    break;
                mob.attackEntityFrom(source, 4F);

                if (infusedSet)
                    mob.addPotionEffect(new EffectInstance(Effects.WITHER, 120));
            }

            BlockPos pos = living.getPosition();
            BlockPos down = pos.down();
            BlockState downState = worldIn.getBlockState(down);

            if (downState.getMaterial() != Material.AIR) {
                SoundType type = downState.getBlock().getSoundType(downState, worldIn, down, null);
                worldIn.playSound(null, pos, type.getBreakSound(), SoundCategory.BLOCKS, type.getVolume() * 0.5F, type.getPitch() * 0.8F);
            }

            PacketHandler.sendToAllAround(worldIn, pos, 32, new PacketParticles((float) living.posX, (float) living.posY, (float) living.posZ, PacketParticles.Type.SHOCKWAVE_CREATOR));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, PlayerEntity player, RenderType type, boolean isHolding) {
        if (type == RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
            GlStateManager.translatef(-0.1675F, -0.05F, armor ? -0.195F : -0.1475F);
            GlStateManager.scalef(0.021F, 0.021F, 0.021F);

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.pushTextureAttributes();
            GlStateManager.pushLightingAttributes();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getInstance().getTextureManager().bindTexture(RES_WORN);
            Screen.blit(0, 0, 0, 0, 16, 16, 16, 16);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttributes();
            GlStateManager.popAttributes();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
