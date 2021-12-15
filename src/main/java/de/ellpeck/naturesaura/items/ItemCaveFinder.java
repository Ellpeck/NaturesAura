package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class ItemCaveFinder extends ItemImpl {

    public ItemCaveFinder() {
        super("cave_finder", new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        var stack = playerIn.getItemInHand(handIn);
        var inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 20000, levelIn.isClientSide))
            return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        if (levelIn.isClientSide) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            inst.setParticleCulling(false);
            var pos = playerIn.blockPosition();
            var range = 30;
            for (var x = -range; x <= range; x++)
                for (var y = -range; y <= range; y++)
                    for (var z = -range; z <= range; z++) {
                        var offset = pos.offset(x, y, z);
                        var state = levelIn.getBlockState(offset);
                        try {
                            if (!state.getBlock().isValidSpawn(state, levelIn, offset, SpawnPlacements.Type.ON_GROUND, null))
                                continue;
                        } catch (Exception e) {
                            continue;
                        }

                        var offUp = offset.above();
                        var stateUp = levelIn.getBlockState(offUp);
                        if (stateUp.isCollisionShapeFullBlock(levelIn, offUp) || stateUp.getMaterial().isLiquid())
                            continue;

                        var sky = levelIn.getBrightness(LightLayer.SKY, offUp);
                        var block = levelIn.getBrightness(LightLayer.BLOCK, offUp);
                        if (sky > 7 || block > 7)
                            continue;

                        inst.spawnMagicParticle(
                                offset.getX() + 0.5F, offset.getY() + 1.5F, offset.getZ() + 0.5F,
                                0F, 0F, 0F, 0x992101, 2.5F, 20 * 30, 0F, false, true);
                    }
            inst.setParticleDepth(true);
            inst.setParticleSpawnRange(32);
            inst.setParticleCulling(true);

            playerIn.swing(handIn);
        }
        playerIn.getCooldowns().addCooldown(this, 20 * 30);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
