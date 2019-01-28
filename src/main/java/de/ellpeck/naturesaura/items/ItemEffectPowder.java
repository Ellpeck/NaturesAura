package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEffectPowder extends ItemImpl implements IColorProvidingItem {

    public ItemEffectPowder() {
        super("effect_powder");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            ResourceLocation effect = getEffect(stack);
            EntityEffectInhibitor entity = new EntityEffectInhibitor(worldIn);
            entity.setInhibitedEffect(effect);
            entity.setColor(NaturesAuraAPI.EFFECT_POWDERS.get(effect));
            entity.setAmount(stack.getCount());
            entity.setPosition(pos.getX() + hitX, pos.getY() + hitY + 1, pos.getZ() + hitZ);
            worldIn.spawnEntity(entity);
            stack.setCount(0);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (ResourceLocation effect : NaturesAuraAPI.EFFECT_POWDERS.keySet()) {
                ItemStack stack = new ItemStack(this);
                setEffect(stack, effect);
                items.add(stack);
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + "." + getEffect(stack) + ".name").trim();
    }

    public static ResourceLocation getEffect(ItemStack stack) {
        if (!stack.hasTagCompound())
            return null;
        String effect = stack.getTagCompound().getString("effect");
        if (effect.isEmpty())
            return null;
        return new ResourceLocation(effect);
    }

    public static ItemStack setEffect(ItemStack stack, ResourceLocation effect) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("effect", effect.toString());
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> NaturesAuraAPI.EFFECT_POWDERS.getOrDefault(getEffect(stack), 0xFFFFFF);
    }
}
