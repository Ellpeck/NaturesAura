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
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && playerIn.isCreative()) {
            var curr = getMultiblock(stack);
            var next = (curr + 1) % multiblocks().size();
            stack.getOrCreateTag().putInt("multiblock", next);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player.isCreative()) {
            var id = getMultiblock(player.getItemInHand(context.getHand()));
            if (id < 0)
                return InteractionResult.PASS;
            var multi = multiblocks().get(id);
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
        var id = getMultiblock(stack);
        if (id < 0)
            return name;
        var multi = multiblocks().get(id);
        return multi == null ? name : name.append(" (" + multi.getName() + ")");
    }

}
