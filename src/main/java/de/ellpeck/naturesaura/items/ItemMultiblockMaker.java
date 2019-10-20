package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn.capabilities.isCreativeMode) {
            int curr = getMultiblock(stack);
            int next = (curr + 1) % multiblocks().size();

            if (!stack.hasTagCompound())
                stack.setTagCompound(new CompoundNBT());
            stack.getTagCompound().setInteger("multiblock", next);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (player.capabilities.isCreativeMode) {
            IMultiblock multi = multiblocks().get(getMultiblock(player.getHeldItem(hand)));
            if (multi == null)
                return ActionResultType.PASS;

            if (!worldIn.isRemote)
                multi.forEach(pos.up(), (char) 0, (blockPos, matcher) -> {
                    worldIn.setBlockState(blockPos, matcher.getDefaultState());
                    return true;
                });

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
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
