package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.InteractionResult;

public class ItemGoldFiber extends ItemImpl implements IColorProvidingItem {

    public ItemGoldFiber() {
        super("gold_fiber");
    }

    @Override
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        if (BlockGoldenLeaves.convert(context.getLevel(), context.getPos())) {
            if (!context.getLevel().isClientSide) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
