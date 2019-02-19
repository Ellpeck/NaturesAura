package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class ItemCaveFinder extends ItemImpl {
    public ItemCaveFinder() {
        super("cave_finder");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, 20000, worldIn.isRemote))
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (worldIn.isRemote) {
            inst.setParticleDepth(false);
            inst.setParticleSpawnRange(64);
            BlockPos pos = playerIn.getPosition();
            int range = 30;
            for (int x = -range; x <= range; x++)
                for (int y = -range; y <= range; y++)
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = pos.add(x, y, z);
                        IBlockState state = worldIn.getBlockState(offset);
                        if (!state.getBlock().canCreatureSpawn(state, worldIn, offset, EntityLiving.SpawnPlacementType.ON_GROUND))
                            continue;

                        BlockPos offUp = offset.up();
                        IBlockState stateUp = worldIn.getBlockState(offUp);
                        if (stateUp.isBlockNormalCube() || stateUp.getMaterial().isLiquid())
                            continue;

                        int sky = worldIn.getLightFor(EnumSkyBlock.SKY, offUp);
                        int block = worldIn.getLightFor(EnumSkyBlock.BLOCK, offUp);
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
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
