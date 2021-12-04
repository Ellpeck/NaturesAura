package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import org.apache.commons.lang3.tuple.Pair;

public class GrassDieEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "grass_die");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot < 0) {
            Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 50);
            int aura = auraAndSpots.getLeft();
            if (aura < 0) {
                this.amount = Math.min(300, MathHelper.ceil(Math.abs(aura) / 100000F / auraAndSpots.getRight()));
                if (this.amount > 1) {
                    this.dist = MathHelper.clamp(Math.abs(aura) / 75000, 5, 75);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.DECAYED_LEAVES);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(level, pos, spot))
            return;
        for (int i = this.amount / 2 + level.rand.nextInt(this.amount / 2); i >= 0; i--) {
            BlockPos grassPos = new BlockPos(
                    pos.getX() + level.rand.nextGaussian() * this.dist,
                    pos.getY() + level.rand.nextGaussian() * this.dist,
                    pos.getZ() + level.rand.nextGaussian() * this.dist
            );
            if (grassPos.distanceSq(pos) <= this.dist * this.dist && level.isBlockLoaded(grassPos)) {
                BlockState state = level.getBlockState(grassPos);
                Block block = state.getBlock();

                BlockState newState = null;
                if (block instanceof LeavesBlock) {
                    newState = ModBlocks.DECAYED_LEAVES.getDefaultState();
                } else if (block instanceof GrassBlock) {
                    newState = Blocks.COARSE_DIRT.getDefaultState();
                } else if (block instanceof BushBlock) {
                    newState = Blocks.AIR.getDefaultState();
                } else if (block == ModBlocks.NETHER_GRASS) {
                    newState = Blocks.NETHERRACK.getDefaultState();
                }
                if (newState != null)
                    level.setBlockState(grassPos, newState);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.grassDieEffect.get() && (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
