package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;

public class ItemLootFinder extends ItemImpl {
    public ItemLootFinder() {
        super("loot_finder");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 100000, false))
            return new ActionResult<>(InteractionResult.FAIL, stack);
        if (levelIn.isClientSide) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            inst.setParticleCulling(false);

            BlockPos pos = playerIn.getPosition();
            Helper.getBlockEntitiesInArea(levelIn, pos, 64, tile -> {
                if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() || tile instanceof MobSpawnerBlockEntity) {
                    inst.spawnMagicParticle(
                            tile.getPos().getX() + 0.5F, tile.getPos().getY() + 0.5F, tile.getPos().getZ() + 0.5F,
                            0F, 0F, 0F, 0xf5f10a, 6F, 20 * 60, 0F, false, true);
                }
                return false;
            });
            for (Entity entity : levelIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(64))) {
                if (!(entity instanceof LivingEntity) && entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                    inst.spawnMagicParticle(
                            entity.getPosX(), entity.getPosYEye(), entity.getPosZ(),
                            0F, 0F, 0F, 0xf5f10a, 6F, 20 * 60, 0F, false, true);
                }
            }

            inst.setParticleDepth(true);
            inst.setParticleSpawnRange(32);
            inst.setParticleCulling(true);

            playerIn.swingArm(handIn);
        }
        playerIn.getCooldownTracker().setCooldown(this, 20 * 60);
        return new ActionResult<>(InteractionResult.SUCCESS, stack);
    }
}
