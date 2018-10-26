package de.ellpeck.naturesaura.aura.chunk.effect;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class GrassDieEffect implements IDrainSpotEffect {
    @Override
    public void update(World world, Chunk chunk, AuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":GrassDieEffect");
        if (spot.intValue() < 0) {
            int aura = AuraChunk.getAuraInArea(world, pos, 25);
            if (aura < 0) {
                int amount = Math.min(300, Math.abs(aura) / 1000);
                if (amount > 1) {
                    int dist = MathHelper.clamp(Math.abs(aura) / 750, 5, 45);
                    if (dist > 0) {
                        for (int i = amount / 2 + world.rand.nextInt(amount / 2); i >= 0; i--) {
                            BlockPos grassPos = new BlockPos(
                                    pos.getX() + world.rand.nextGaussian() * dist,
                                    pos.getY() + world.rand.nextGaussian() * dist,
                                    pos.getZ() + world.rand.nextGaussian() * dist
                            );
                            if (grassPos.distanceSq(pos) <= dist * dist && world.isBlockLoaded(grassPos)) {
                                IBlockState state = world.getBlockState(grassPos);
                                Block block = state.getBlock();

                                IBlockState newState = null;
                                if (block instanceof BlockLeaves) {
                                    newState = ModBlocks.DECAYED_LEAVES.getDefaultState();
                                } else if (block instanceof BlockGrass) {
                                    newState = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
                                } else if (block instanceof BlockBush) {
                                    newState = Blocks.AIR.getDefaultState();
                                }
                                if (newState != null)
                                    world.setBlockState(grassPos, newState);
                            }
                        }
                    }
                }
            }
        }
        world.profiler.endSection();
    }
}
