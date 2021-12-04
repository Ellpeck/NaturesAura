package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.*;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.gen.Heightmap;
import net.minecraft.level.server.ServerLevel;
import org.apache.commons.lang3.tuple.Pair;

public class PlantBoostEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "plant_boost");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        this.amount = Math.min(45, Mth.ceil(Math.abs(aura) / 100000F / auraAndSpots.getRight()));
        if (this.amount <= 1)
            return false;
        this.dist = Mth.clamp(Math.abs(aura) / 150000, 5, 35);
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
        return new ItemStack(Items.WHEAT_SEEDS);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(level, pos, spot))
            return;
        for (int i = this.amount / 2 + level.rand.nextInt(this.amount / 2); i >= 0; i--) {
            int x = Mth.floor(pos.getX() + (2 * level.rand.nextFloat() - 1) * this.dist);
            int z = Mth.floor(pos.getZ() + (2 * level.rand.nextFloat() - 1) * this.dist);
            BlockPos plantPos = new BlockPos(x, level.getHeight(Heightmap.Type.WORLD_SURFACE, x, z), z).down();
            if (plantPos.distanceSq(pos) <= this.dist * this.dist && level.isBlockLoaded(plantPos)) {
                if (NaturesAuraAPI.instance().isEffectPowderActive(level, plantPos, NAME))
                    continue;

                BlockState state = level.getBlockState(plantPos);
                Block block = state.getBlock();
                if (block instanceof IGrowable && !(block instanceof DoublePlantBlock) && !(block instanceof TallGrassBlock) && block != Blocks.GRASS_BLOCK) {
                    IGrowable growable = (IGrowable) block;
                    if (growable.canGrow(level, plantPos, state, false)) {
                        try {
                            growable.grow((ServerLevel) level, level.rand, plantPos, state);
                        } catch (Exception e) {
                            // a lot of stuff throws here (double plants where generation only caused half of it to exist, bamboo at level height...)
                            // so just catch all, bleh
                        }
                        BlockPos closestSpot = IAuraChunk.getHighestSpot(level, plantPos, 25, pos);
                        IAuraChunk.getAuraChunk(level, closestSpot).drainAura(closestSpot, 3500);

                        PacketHandler.sendToAllAround(level, plantPos, 32,
                                new PacketParticles(plantPos.getX(), plantPos.getY(), plantPos.getZ(), PacketParticles.Type.PLANT_BOOST));
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.plantBoostEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
