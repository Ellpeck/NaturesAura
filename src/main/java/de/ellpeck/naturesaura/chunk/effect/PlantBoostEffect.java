package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
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
        return new ItemStack(Items.WHEAT_SEEDS);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(level, pos, spot))
            return;
        for (int i = this.amount / 2 + level.random.nextInt(this.amount / 2); i >= 0; i--) {
            int x = Mth.floor(pos.getX() + (2 * level.random.nextFloat() - 1) * this.dist);
            int z = Mth.floor(pos.getZ() + (2 * level.random.nextFloat() - 1) * this.dist);
            BlockPos plantPos = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z).below();
            if (plantPos.distSqr(pos) <= this.dist * this.dist && level.isLoaded(plantPos)) {
                if (NaturesAuraAPI.instance().isEffectPowderActive(level, plantPos, NAME))
                    continue;

                BlockState state = level.getBlockState(plantPos);
                Block block = state.getBlock();
                if (block instanceof BonemealableBlock growable && !(block instanceof DoublePlantBlock) && !(block instanceof TallGrassBlock) && block != Blocks.GRASS_BLOCK) {
                    if (growable.isValidBonemealTarget(level, plantPos, state, false)) {
                        try {
                            growable.performBonemeal((ServerLevel) level, level.random, plantPos, state);
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
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.plantBoostEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
