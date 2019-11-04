package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MultiblockMaker extends ItemImpl {

    private static List<IMultiblock> multiblocks;

    public MultiblockMaker() {
        super("multiblock_maker", new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn.isCreative()) {
            int curr = getMultiblock(stack);
            int next = (curr + 1) % multiblocks().size();
            stack.getOrCreateTag().putInt("multiblock", next);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player.isCreative()) {
            IMultiblock multi = multiblocks().get(getMultiblock(player.getHeldItem(context.getHand())));
            if (multi == null)
                return ActionResultType.PASS;

            if (!context.getWorld().isRemote)
                multi.forEach(context.getPos().up(), (char) 0, (blockPos, matcher) -> {
                    context.getWorld().setBlockState(blockPos, matcher.getDefaultState());
                    return true;
                });

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent name = super.getDisplayName(stack);
        IMultiblock multi = multiblocks().get(getMultiblock(stack));
        return multi == null ? name : name.appendText(" (" + multi.getName() + ")");
    }

    private static int getMultiblock(ItemStack stack) {
        if (!stack.hasTag())
            return 0;
        return stack.getTag().getInt("multiblock");
    }

    private static List<IMultiblock> multiblocks() {
        if (multiblocks == null) {
            multiblocks = new ArrayList<>();
            multiblocks.addAll(NaturesAuraAPI.MULTIBLOCKS.values());
        }
        return multiblocks;
    }
}
