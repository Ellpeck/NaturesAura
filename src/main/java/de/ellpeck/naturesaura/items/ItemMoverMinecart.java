package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemMoverMinecart extends ItemImpl {

    public ItemMoverMinecart() {
        super("mover_cart");
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (AbstractRailBlock.isRailBlock(world.getBlockState(pos))) {
            if (!world.isRemote) {
                AbstractMinecartEntity cart = new EntityMoverMinecart(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(cart);
            }
            player.getHeldItem(hand).shrink(1);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
