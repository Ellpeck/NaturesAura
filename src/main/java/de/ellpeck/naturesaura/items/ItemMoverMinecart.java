package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;

import javax.annotation.Nonnull;

public class ItemMoverMinecart extends ItemImpl {

    public ItemMoverMinecart() {
        super("mover_cart", new Properties().maxStackSize(1));
    }

    @Nonnull
    @Override
    public InteractionResult onItemUse(ItemUseContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getPos();
        if (AbstractRailBlock.isRail(level.getBlockState(pos))) {
            if (!level.isClientSide) {
                AbstractMinecartEntity cart = new EntityMoverMinecart(ModEntities.MOVER_CART, level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.addEntity(cart);
            }
            context.getPlayer().getHeldItem(context.getHand()).shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
