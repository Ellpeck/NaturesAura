package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.blocks.BlockGoldenLeaves;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGoldFiber extends ItemImpl implements IColorProvidingItem {

    public ItemGoldFiber() {
        super("gold_fiber");
    }

    @Override
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (BlockGoldenLeaves.convert(worldIn, pos)) {
            if (!worldIn.isRemote) {
                stack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
