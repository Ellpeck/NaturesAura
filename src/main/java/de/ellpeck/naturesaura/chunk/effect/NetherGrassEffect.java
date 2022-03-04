package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.tuple.Pair;

public class NetherGrassEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "nether_grass");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        var auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        this.amount = Math.min(20, Mth.ceil(Math.abs(aura) / 100000F / auraAndSpots.getRight()));
        if (this.amount <= 1)
            return false;
        this.dist = Mth.clamp(Math.abs(aura) / 100000, 5, 35);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.blockPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.NETHER_GRASS);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        for (var i = this.amount / 2 + level.random.nextInt(this.amount / 2); i >= 0; i--) {
            var x = Mth.floor(pos.getX() + level.random.nextGaussian() * this.dist);
            var y = Mth.floor(pos.getY() + level.random.nextGaussian() * this.dist);
            var z = Mth.floor(pos.getZ() + level.random.nextGaussian() * this.dist);

            for (var yOff = -5; yOff <= 5; yOff++) {
                var goalPos = new BlockPos(x, y + yOff, z);
                if (goalPos.distSqr(pos) <= this.dist * this.dist && level.isLoaded(goalPos)) {
                    if (NaturesAuraAPI.instance().isEffectPowderActive(level, goalPos, NAME))
                        continue;
                    var up = goalPos.above();
                    if (level.getBlockState(up).isFaceSturdy(level, up, Direction.DOWN))
                        continue;

                    var state = level.getBlockState(goalPos);
                    if (state.is(Tags.Blocks.NETHERRACK)) {
                        level.setBlockAndUpdate(goalPos, ModBlocks.NETHER_GRASS.defaultBlockState());

                        var closestSpot = IAuraChunk.getHighestSpot(level, goalPos, 25, pos);
                        IAuraChunk.getAuraChunk(level, closestSpot).drainAura(closestSpot, 500);

                        PacketHandler.sendToAllAround(level, goalPos, 32,
                                new PacketParticles(goalPos.getX(), goalPos.getY() + 0.5F, goalPos.getZ(), PacketParticles.Type.PLANT_BOOST));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.netherGrassEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_NETHER);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
