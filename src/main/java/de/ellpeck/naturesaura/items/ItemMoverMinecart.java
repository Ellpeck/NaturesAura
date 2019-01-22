package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (BlockRailBase.isRailBlock(world.getBlockState(pos))) {
            if (!world.isRemote) {
                EntityMinecart cart = new EntityMoverMinecart(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.spawnEntity(cart);
            }
            player.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
