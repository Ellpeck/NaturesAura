package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.renderers.ITrinketItem;
import de.ellpeck.naturesaura.renderers.PlayerLayerTrinkets;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemShockwaveCreator extends ItemImpl implements ITrinketItem {

    @SideOnly(Side.CLIENT)
    private static final ResourceLocation RES_WORN = new ResourceLocation(NaturesAura.MOD_ID, "textures/items/shockwave_creator_player.png");

    public ItemShockwaveCreator() {
        super("shockwave_creator");
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || !(entityIn instanceof EntityLivingBase))
            return;
        EntityLivingBase living = (EntityLivingBase) entityIn;
        if (!living.onGround) {
            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.getBoolean("air"))
                return;

            compound.setBoolean("air", true);
            compound.setDouble("x", living.posX);
            compound.setDouble("y", living.posY);
            compound.setDouble("z", living.posZ);
        } else {
            if (!stack.hasTagCompound())
                return;
            NBTTagCompound compound = stack.getTagCompound();
            if (!compound.getBoolean("air"))
                return;

            compound.setBoolean("air", false);

            if (!living.isSneaking())
                return;
            if (living.getDistanceSq(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z")) > 0.75F)
                return;
            if (living instanceof EntityPlayer && !Helper.extractAuraFromPlayer((EntityPlayer) living, 10, false))
                return;

            DamageSource source;
            if (living instanceof EntityPlayer)
                source = DamageSource.causePlayerDamage((EntityPlayer) living);
            else
                source = DamageSource.MAGIC;

            int range = 5;
            List<EntityLiving> mobs = worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(
                    living.posX - range, living.posY - 0.5, living.posZ - range,
                    living.posX + range, living.posY + 0.5, living.posZ + range));
            for (EntityLiving mob : mobs) {
                if (mob.isDead || mob == living)
                    continue;
                if (living.getDistanceSq(mob) > range * range)
                    continue;
                if (living instanceof EntityPlayer && !Helper.extractAuraFromPlayer((EntityPlayer) living, 5, false))
                    break;
                mob.attackEntityFrom(source, 4F);
            }

            BlockPos pos = living.getPosition();
            BlockPos down = pos.down();
            IBlockState downState = worldIn.getBlockState(down);

            if (downState.getMaterial() != Material.AIR) {
                SoundType type = downState.getBlock().getSoundType(downState, worldIn, down, null);
                worldIn.playSound(null, pos, type.getBreakSound(), SoundCategory.BLOCKS, type.getVolume() * 0.5F, type.getPitch() * 0.8F);
            }
            if (worldIn instanceof WorldServer)
                ((WorldServer) worldIn).spawnParticle(EnumParticleTypes.BLOCK_DUST,
                        living.posX, living.posY + 0.01F, living.posZ,
                        15, 0F, 0F, 0F, 0.15F, Block.getStateId(downState));
            PacketHandler.sendToAllAround(worldIn, pos, 32,
                    new PacketParticles((float) living.posX, (float) living.posY, (float) living.posZ, 11));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(ItemStack stack, EntityPlayer player, PlayerLayerTrinkets.RenderType type, boolean isHolding) {
        if (type == PlayerLayerTrinkets.RenderType.BODY && !isHolding) {
            boolean armor = !player.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex()).isEmpty();
            GlStateManager.translate(-0.1675F, -0.05F, armor ? -0.195F : -0.13F);
            GlStateManager.scale(0.021F, 0.021F, 0.021F);

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getTextureManager().bindTexture(RES_WORN);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
