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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.tuple.Pair;

public class NetherGrassEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "nether_grass");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
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
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.NETHER_GRASS);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        for (int i = this.amount / 2 + level.rand.nextInt(this.amount / 2); i >= 0; i--) {
            int x = Mth.floor(pos.getX() + level.rand.nextGaussian() * this.dist);
            int y = Mth.floor(pos.getY() + level.rand.nextGaussian() * this.dist);
            int z = Mth.floor(pos.getZ() + level.rand.nextGaussian() * this.dist);

            for (int yOff = -5; yOff <= 5; yOff++) {
                BlockPos goalPos = new BlockPos(x, y + yOff, z);
                if (goalPos.distanceSq(pos) <= this.dist * this.dist && level.isBlockLoaded(goalPos)) {
                    if (NaturesAuraAPI.instance().isEffectPowderActive(level, goalPos, NAME))
                        continue;
                    BlockPos up = goalPos.up();
                    if (level.getBlockState(up).isSolidSide(level, up, Direction.DOWN))
                        continue;

                    BlockState state = level.getBlockState(goalPos);
                    Block block = state.getBlock();
                    if (Tags.Blocks.NETHERRACK.contains(block)) {
                        level.setBlockState(goalPos, ModBlocks.NETHER_GRASS.getDefaultState());

                        BlockPos closestSpot = IAuraChunk.getHighestSpot(level, goalPos, 25, pos);
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
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.netherGrassEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_NETHER);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
