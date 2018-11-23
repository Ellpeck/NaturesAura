package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemMultiblockMaker extends ItemImpl {

    private static List<IMultiblock> multiblocks;

    public ItemMultiblockMaker() {
        super("multiblock_maker");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn.capabilities.isCreativeMode) {
            int curr = getMultiblock(stack);
            int next = (curr + 1) % multiblocks().size();

            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("multiblock", next);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.capabilities.isCreativeMode) {
            IMultiblock multi = multiblocks().get(getMultiblock(player.getHeldItem(hand)));
            if (multi == null)
                return EnumActionResult.PASS;

            if (!worldIn.isRemote)
                multi.forEach(pos.up(), (char) 0, (blockPos, matcher) -> {
                    worldIn.setBlockState(blockPos, matcher.getDefaultState());
                    return true;
                });

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        IMultiblock multi = multiblocks().get(getMultiblock(stack));
        return multi == null ? name : name + " (" + multi.getName() + ")";
    }

    private static int getMultiblock(ItemStack stack) {
        if (!stack.hasTagCompound())
            return 0;
        return stack.getTagCompound().getInteger("multiblock");
    }

    private static List<IMultiblock> multiblocks() {
        if (multiblocks == null) {
            multiblocks = new ArrayList<>();
            multiblocks.addAll(NaturesAuraAPI.MULTIBLOCKS.values());
        }
        return multiblocks;
    }
}
