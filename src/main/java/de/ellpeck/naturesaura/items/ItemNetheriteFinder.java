package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;

public class ItemNetheriteFinder extends ItemImpl {
    public ItemNetheriteFinder() {
        super("netherite_finder", new Properties().maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 200000, false))
            return new ActionResult<>(InteractionResult.FAIL, stack);
        if (levelIn.isClientSide) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            inst.setParticleCulling(false);

            BlockPos pos = playerIn.getPosition();
            int range = 12;
            for (int x = -range; x <= range; x++) {
                for (int y = 0; y <= 128; y++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
                        BlockState state = levelIn.getBlockState(offset);
                        if (state.getBlock() == Blocks.ANCIENT_DEBRIS || state.getBlock().getRegistryName().toString().contains("netherite")) {
                            inst.spawnMagicParticle(
                                    offset.getX() + 0.5F, offset.getY() + 0.5F, offset.getZ() + 0.5F,
                                    0F, 0F, 0F, 0xab4d38, 6F, 20 * 60, 0F, false, true);
                        }
                    }
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
