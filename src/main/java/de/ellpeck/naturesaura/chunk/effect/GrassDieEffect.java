package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public class GrassDieEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "grass_die");

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, MutableInt spot) {
        if (spot.intValue() < 0) {
            int aura = IAuraChunk.getAuraInArea(world, pos, 50);
            if (aura < 0) {
                int amount = Math.min(300, Math.abs(aura) / 1000);
                if (amount > 1) {
                    int dist = MathHelper.clamp(Math.abs(aura) / 750, 5, 75);
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

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.grassDieEffect && type == NaturesAuraAPI.TYPE_OVERWORLD;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
