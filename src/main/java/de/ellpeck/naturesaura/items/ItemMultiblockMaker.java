package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemMultiblockMaker extends ItemImpl {

    private static List<IMultiblock> multiblocks;

    public ItemMultiblockMaker() {
        super("multiblock_maker");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && playerIn.isCreative()) {
            var curr = ItemMultiblockMaker.getMultiblockId(stack);
            var next = (curr + 1) % ItemMultiblockMaker.multiblocks().size();
            stack.getOrCreateTag().putInt("multiblock", next);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player.isCreative()) {
            var multi = ItemMultiblockMaker.getMultiblock(player.getItemInHand(context.getHand()));
            if (multi == null)
                return InteractionResult.PASS;

            if (!context.getLevel().isClientSide)
                multi.forEach(context.getClickedPos().above(), (char) 0, (blockPos, matcher) -> {
                    context.getLevel().setBlockAndUpdate(blockPos, matcher.defaultState());
                    return true;
                });

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = (MutableComponent) super.getName(stack);
        var multi = ItemMultiblockMaker.getMultiblock(stack);
        return multi == null ? name : name.append(" (" + multi.getName() + ")");
    }

    private static List<IMultiblock> multiblocks() {
        if (ItemMultiblockMaker.multiblocks == null) {
            // some weird mixins call getName way too early, before multiblocks are initialized
            if (NaturesAuraAPI.MULTIBLOCKS.isEmpty())
                return null;
            ItemMultiblockMaker.multiblocks = new ArrayList<>();
            ItemMultiblockMaker.multiblocks.addAll(NaturesAuraAPI.MULTIBLOCKS.values());
        }
        return ItemMultiblockMaker.multiblocks;
    }

    private static int getMultiblockId(ItemStack stack) {
        if (!stack.hasTag())
            return -1;
        return stack.getTag().getInt("multiblock");
    }

    private static IMultiblock getMultiblock(ItemStack stack) {
        var multiblocks = ItemMultiblockMaker.multiblocks();
        if (multiblocks == null)
            return null;
        var id = ItemMultiblockMaker.getMultiblockId(stack);
        if (id < 0 || id >= multiblocks.size())
            return null;
        return multiblocks.get(id);
    }
}
