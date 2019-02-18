package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemEnderAccess extends ItemImpl {
    public ItemEnderAccess() {
        super("ender_access");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (handIn != EnumHand.MAIN_HAND)
            return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (stack.hasDisplayName()) {
            if (!worldIn.isRemote && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 5000, false)) {
                playerIn.openGui(NaturesAura.MOD_ID, 1, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
}
