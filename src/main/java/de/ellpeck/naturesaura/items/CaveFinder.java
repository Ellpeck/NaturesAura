package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class CaveFinder extends ItemImpl {
    public CaveFinder() {
        super("cave_finder", new Properties().maxStackSize(1).group(NaturesAura.CREATIVE_TAB));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 20000, worldIn.isRemote))
            return new ActionResult<>(ActionResultType.FAIL, stack);
        if (worldIn.isRemote) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            BlockPos pos = playerIn.getPosition();
            int range = 30;
            for (int x = -range; x <= range; x++)
                for (int y = -range; y <= range; y++)
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = pos.add(x, y, z);
                        BlockState state = worldIn.getBlockState(offset);
                        if (!state.getBlock().canCreatureSpawn(state, worldIn, offset, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, null))
                            continue;

                        BlockPos offUp = offset.up();
                        BlockState stateUp = worldIn.getBlockState(offUp);
                        if (stateUp.isNormalCube(worldIn, offUp) || stateUp.getMaterial().isLiquid())
                            continue;

                        int sky = worldIn.getLightFor(LightType.SKY, offUp);
                        int block = worldIn.getLightFor(LightType.BLOCK, offUp);
                        if (sky > 7 || block > 7)
                            continue;

                        inst.spawnMagicParticle(
                                offset.getX() + 0.5F, offset.getY() + 1.5F, offset.getZ() + 0.5F,
                                0F, 0F, 0F, 0x992101, 2.5F, 20 * 30, 0F, false, true);
                    }
            inst.setParticleDepth(true);
            inst.setParticleSpawnRange(32);

            playerIn.swingArm(handIn);
        }
        playerIn.getCooldownTracker().setCooldown(this, 20 * 30);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
