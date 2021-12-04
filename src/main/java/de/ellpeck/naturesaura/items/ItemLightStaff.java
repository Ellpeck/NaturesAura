package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityLightProjectile;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.level.Level;

public class ItemLightStaff extends ItemImpl {
    public ItemLightStaff() {
        super("light_staff");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!levelIn.isClientSide && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 1000, false)) {
            EntityLightProjectile projectile = new EntityLightProjectile(ModEntities.LIGHT_PROJECTILE, playerIn, levelIn);
            projectile.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 0);
            levelIn.addEntity(projectile);
        }
        return new ActionResult<>(InteractionResult.SUCCESS, stack);
    }
}
