package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GoldFiber extends ItemImpl implements IColorProvidingItem {

    public GoldFiber() {
        super("gold_fiber", new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        if (BlockGoldenLeaves.convert(context.getWorld(), context.getPos())) {
            if (!context.getWorld().isRemote) {
                stack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
