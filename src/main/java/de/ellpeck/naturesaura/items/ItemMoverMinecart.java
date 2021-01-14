package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemMoverMinecart extends ItemImpl {

    public ItemMoverMinecart() {
        super("mover_cart", new Properties().maxStackSize(1));
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        if (AbstractRailBlock.isRail(world.getBlockState(pos))) {
            if (!world.isRemote) {
                AbstractMinecartEntity cart = new EntityMoverMinecart(ModEntities.MOVER_CART, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.addEntity(cart);
            }
            context.getPlayer().getHeldItem(context.getHand()).shrink(1);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
