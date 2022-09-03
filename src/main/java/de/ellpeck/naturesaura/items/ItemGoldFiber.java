package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class ItemGoldFiber extends ItemImpl implements IColorProvidingItem {

    public ItemGoldFiber() {
        super("gold_fiber");
    }

    @Override
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var stack = context.getPlayer().getItemInHand(context.getHand());
        if (BlockGoldenLeaves.convert(context.getLevel(), context.getClickedPos())) {
            if (!context.getLevel().isClientSide) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
