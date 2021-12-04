package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.WeightedOre;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.Player;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.server.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreSpawnEffect implements IDrainSpotEffect {

    public static final Set<BlockState> SPAWN_EXCEPTIONS = new HashSet<>();
    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "ore_spawn");

    private int amount;
    private int dist;

    private boolean calcValues(Level level, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura <= 2000000)
            return false;
        this.amount = Math.min(20, MathHelper.ceil(Math.abs(aura) / 300000F / auraAndSpots.getRight()));
        if (this.amount <= 0)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 150000, 5, 20);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.DIAMOND_ORE);
    }

    @Override
    public void update(Level level, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        IAuraType type = auraChunk.getType();
        Block requiredBlock;
        List<WeightedOre> ores;
        if (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)) {
            requiredBlock = Blocks.STONE;
            ores = NaturesAuraAPI.OVERWORLD_ORES;
        } else {
            requiredBlock = Blocks.NETHERRACK;
            ores = NaturesAuraAPI.NETHER_ORES;
        }
        int totalWeight = WeightedRandom.getTotalWeight(ores);

        List<Tuple<Vector3d, Integer>> powders = NaturesAuraAPI.instance().getActiveEffectPowders(level,
                new AxisAlignedBB(pos).grow(this.dist), NAME);
        if (powders.isEmpty())
            return;
        for (int i = 0; i < this.amount; i++) {
            Tuple<Vector3d, Integer> powder = powders.get(i % powders.size());
            Vector3d powderPos = powder.getA();
            int range = powder.getB();
            int x = MathHelper.floor(powderPos.x + level.rand.nextGaussian() * range);
            int y = MathHelper.floor(powderPos.y + level.rand.nextGaussian() * range);
            int z = MathHelper.floor(powderPos.z + level.rand.nextGaussian() * range);
            BlockPos orePos = new BlockPos(x, y, z);
            if (orePos.distanceSq(powderPos.x, powderPos.y, powderPos.z, true) <= range * range
                    && orePos.distanceSq(pos) <= this.dist * this.dist && level.isBlockLoaded(orePos)) {
                BlockState state = level.getBlockState(orePos);
                if (state.getBlock() != requiredBlock)
                    continue;

                outer:
                while (true) {
                    WeightedOre ore = WeightedRandom.getRandomItem(level.rand, ores, totalWeight);
                    ITag<Block> tag = level.getTags().func_241835_a().get(ore.tag);
                    if (tag == null)
                        continue;
                    for (Block toPlace : tag.getAllElements()) {
                        if (toPlace == null || toPlace == Blocks.AIR)
                            continue;

                        FakePlayer player = FakePlayerFactory.getMinecraft((ServerLevel) level);
                        player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                        BlockRayTraceResult ray = new BlockRayTraceResult(Vector3d.copyCentered(pos), Direction.UP, pos, false);
                        BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, ray));
                        BlockState stateToPlace = toPlace.getStateForPlacement(context);
                        if (SPAWN_EXCEPTIONS.contains(stateToPlace))
                            continue;

                        level.setBlockState(orePos, stateToPlace);
                        level.playEvent(2001, orePos, Block.getStateId(stateToPlace));

                        int toDrain = (20000 - ore.itemWeight * 2) * 2;
                        BlockPos highestSpot = IAuraChunk.getHighestSpot(level, orePos, 30, pos);
                        IAuraChunk.getAuraChunk(level, highestSpot).drainAura(highestSpot, toDrain);
                        break outer;
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.oreEffect.get() &&
                (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
