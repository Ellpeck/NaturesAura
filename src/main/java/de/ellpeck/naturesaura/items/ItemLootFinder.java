package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class ItemLootFinder extends ItemImpl {
    public ItemLootFinder() {
        super("loot_finder");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 100000, false))
            return new ActionResult<>(ActionResultType.FAIL, stack);
        if (worldIn.isRemote) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            inst.setParticleCulling(false);

            BlockPos pos = playerIn.getPosition();
            Helper.getTileEntitiesInArea(worldIn, pos, 64, tile -> {
                if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() || tile instanceof MobSpawnerTileEntity) {
                    inst.spawnMagicParticle(
                            tile.getPos().getX() + 0.5F, tile.getPos().getY() + 0.5F, tile.getPos().getZ() + 0.5F,
                            0F, 0F, 0F, 0xf5f10a, 6F, 20 * 60, 0F, false, true);
                }
                return false;
            });
            for (Entity entity : worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(64))) {
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
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
