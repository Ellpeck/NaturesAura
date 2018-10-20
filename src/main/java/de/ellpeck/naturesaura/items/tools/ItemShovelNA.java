package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class ItemShovelNA extends ItemSpade implements IModItem, IModelProvider {
    private final String baseName;

    public ItemShovelNA(String baseName, ToolMaterial material) {
        super(material);
        this.baseName = baseName;
        ModRegistry.addItemOrBlock(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult result = EnumActionResult.PASS;
        if (this == ModItems.INFUSED_SHOVEL) {
            ItemStack stack = player.getHeldItem(hand);
            int range = player.isSneaking() ? 0 : 1;
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    BlockPos actualPos = pos.add(x, 0, y);
                    if (player.canPlayerEdit(actualPos.offset(facing), facing, stack)) {
                        if (facing != EnumFacing.DOWN
                                && worldIn.getBlockState(actualPos.up()).getMaterial() == Material.AIR
                                && worldIn.getBlockState(actualPos).getBlock() == Blocks.GRASS) {
                            if (!worldIn.isRemote) {
                                worldIn.setBlockState(actualPos, Blocks.GRASS_PATH.getDefaultState(), 11);
                            }
                            result = EnumActionResult.SUCCESS;
                        }
                    }
                }
            }

            if (result == EnumActionResult.SUCCESS) {
                worldIn.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                stack.damageItem(1, player);
            }
        }
        return result;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (this == ModItems.INFUSED_SHOVEL)
            return Helper.makeRechargeProvider(stack);
        else return null;
    }
}
