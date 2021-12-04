package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemMultiblockMaker extends ItemImpl {

    private static List<IMultiblock> multiblocks;

    public ItemMultiblockMaker() {
        super("multiblock_maker");
    }

    private static int getMultiblock(ItemStack stack) {
        if (!stack.hasTag())
            return -1;
        return stack.getTag().getInt("multiblock");
    }

    private static List<IMultiblock> multiblocks() {
        if (multiblocks == null) {
            multiblocks = new ArrayList<>();
            multiblocks.addAll(NaturesAuraAPI.MULTIBLOCKS.values());
        }
        return multiblocks;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!levelIn.isClientSide && playerIn.isCreative()) {
            int curr = getMultiblock(stack);
            int next = (curr + 1) % multiblocks().size();
            stack.getOrCreateTag().putInt("multiblock", next);
        }
        return new ActionResult<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        Player player = context.getPlayer();
        if (player.isCreative()) {
            int id = getMultiblock(player.getHeldItem(context.getHand()));
            if (id < 0)
                return InteractionResult.PASS;
            IMultiblock multi = multiblocks().get(id);
            if (multi == null)
                return InteractionResult.PASS;

            if (!context.getLevel().isClientSide)
                multi.forEach(context.getPos().up(), (char) 0, (blockPos, matcher) -> {
                    context.getLevel().setBlockState(blockPos, matcher.getDefaultState());
                    return true;
                });

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        TextComponent name = (TextComponent) super.getDisplayName(stack);
        int id = getMultiblock(stack);
        if (id < 0)
            return name;
        IMultiblock multi = multiblocks().get(id);
        return multi == null ? name : name.appendString(" (" + multi.getName() + ")");
    }
}
