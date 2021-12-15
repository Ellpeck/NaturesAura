package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.entities.EntityLightProjectile;
import de.ellpeck.naturesaura.entities.ModEntities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemLightStaff extends ItemImpl {

    public ItemLightStaff() {
        super("light_staff");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        if (!levelIn.isClientSide && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 1000, false)) {
            var projectile = new EntityLightProjectile(ModEntities.LIGHT_PROJECTILE, playerIn, levelIn);
            projectile.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0, 1.5F, 0);
            levelIn.addFreshEntity(projectile);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
