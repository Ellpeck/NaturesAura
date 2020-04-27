package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityLightProjectile;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemLightStaff extends ItemImpl {
    public ItemLightStaff() {
        super("light_staff");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 1000, false)) {
            EntityLightProjectile projectile = new EntityLightProjectile(ModEntities.LIGHT_PROJECTILE, playerIn, worldIn);
            projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 0);
            worldIn.addEntity(projectile);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
