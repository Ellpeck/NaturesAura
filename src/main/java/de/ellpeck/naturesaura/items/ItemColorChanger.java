package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemColorChanger extends ItemImpl implements IColorProvidingItem {

    public ItemColorChanger() {
        super("color_changer");
        this.setMaxStackSize(1);

        this.addPropertyOverride(new ResourceLocation(NaturesAura.MOD_ID, "fill_mode"),
                (stack, worldIn, entityIn) -> isFillMode(stack) ? 1F : 0F);
        this.addPropertyOverride(new ResourceLocation(NaturesAura.MOD_ID, "has_color"),
                (stack, worldIn, entityIn) -> getStoredColor(stack) != null ? 1F : 0F);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (changeOrCopyColor(player, stack, worldIn, pos, null)) {
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.PASS;
        }
    }

    private static boolean changeOrCopyColor(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumDyeColor firstColor) {
        IBlockState state = world.getBlockState(pos);
        for (IProperty prop : state.getProperties().keySet()) {
            if (prop.getValueClass() == EnumDyeColor.class) {
                EnumDyeColor color = (EnumDyeColor) state.getValue(prop);
                if (firstColor == null || color == firstColor) {
                    EnumDyeColor stored = getStoredColor(stack);
                    if (player.isSneaking()) {
                        if (stored != color) {
                            world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.65F, 1F);
                            if (!world.isRemote)
                                storeColor(stack, color);
                            return true;
                        }
                    } else {
                        if (stored != null && stored != color) {
                            if (NaturesAuraAPI.instance().extractAuraFromPlayer(player, 1000, world.isRemote)) {
                                if (firstColor == null) {
                                    world.playSound(player, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                            SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 0.65F, 1F);
                                }
                                if (!world.isRemote) {
                                    world.setBlockState(pos, state.withProperty(prop, stored));

                                    if (isFillMode(stack)) {
                                        for (EnumFacing off : EnumFacing.VALUES) {
                                            changeOrCopyColor(player, stack, world, pos.offset(off), color);
                                        }
                                    }
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking() && getStoredColor(stack) != null) {
            worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.PLAYERS, 0.65F, 1F);
            if (!worldIn.isRemote) {
                setFillMode(stack, !isFillMode(stack));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }


    private static EnumDyeColor getStoredColor(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        } else {
            int color = stack.getTagCompound().getInteger("color");
            return EnumDyeColor.byMetadata(color);
        }
    }

    private static void storeColor(ItemStack stack, EnumDyeColor color) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("color", color.getMetadata());
    }

    private static boolean isFillMode(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        } else {
            return stack.getTagCompound().getBoolean("fill");
        }
    }

    private static void setFillMode(ItemStack stack, boolean fill) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("fill", fill);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> {
            if (tintIndex > 0) {
                EnumDyeColor color = getStoredColor(stack);
                if (color != null) {
                    return color.getColorValue();
                }
            }
            return 0xFFFFFF;
        };
    }
}
