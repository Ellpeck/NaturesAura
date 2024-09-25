package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class GrassDieEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "grass_die");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot < 0) {
            var auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 50);
            int aura = auraAndSpots.getLeft();
            if (aura < 0) {
                this.amount = Math.min(300, Mth.ceil(Math.abs(aura) / 100000F / auraAndSpots.getRight()));
                if (this.amount > 1) {
                    this.dist = Mth.clamp(Math.abs(aura) / 75000, 5, 75);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level(), pos, spot))
            return ActiveType.INACTIVE;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.DECAYED_LEAVES);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot) {
        if (!this.calcValues(level, pos, spot))
            return;
        for (var i = this.amount / 2 + level.random.nextInt(this.amount / 2); i >= 0; i--) {
            var grassPos = BlockPos.containing(
                pos.getX() + level.random.nextGaussian() * this.dist,
                pos.getY() + level.random.nextGaussian() * this.dist,
                pos.getZ() + level.random.nextGaussian() * this.dist
            );
            if (grassPos.distSqr(pos) <= this.dist * this.dist && level.isLoaded(grassPos)) {
                var state = level.getBlockState(grassPos);
                var block = state.getBlock();

                BlockState newState = null;
                if (block instanceof LeavesBlock) {
                    newState = ModBlocks.DECAYED_LEAVES.defaultBlockState();
                } else if (block instanceof GrassBlock) {
                    newState = Blocks.COARSE_DIRT.defaultBlockState();
                } else if (block instanceof BushBlock) {
                    newState = Blocks.AIR.defaultBlockState();
                } else if (block == ModBlocks.NETHER_GRASS) {
                    newState = Blocks.NETHERRACK.defaultBlockState();
                }
                if (newState != null)
                    level.setBlockAndUpdate(grassPos, newState);
            }
        }
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.grassDieEffect.get() && (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return GrassDieEffect.NAME;
    }

}
