package de.ellpeck.naturesaura.items;

import com.google.common.base.Strings;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.BlockEnderCrate;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderAccess extends ItemImpl {
    public ItemEnderAccess() {
        super("ender_access");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (handIn != Hand.MAIN_HAND)
            return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (!Strings.isNullOrEmpty(BlockEnderCrate.getEnderName(stack))) {
            if (!worldIn.isRemote && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 10000, false))
                playerIn.openGui(NaturesAura.MOD_ID, 1, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        BlockEnderCrate.addEnderNameInfo(stack, tooltip);
    }
}
