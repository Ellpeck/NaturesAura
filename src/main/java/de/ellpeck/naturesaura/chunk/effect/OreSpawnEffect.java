package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.WeightedOre;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;

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
        var auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura <= 2000000)
            return false;
        this.amount = Math.min(20, Mth.ceil(Math.abs(aura) / 300000F / auraAndSpots.getRight()));
        if (this.amount <= 0)
            return false;
        this.dist = Mth.clamp(Math.abs(aura) / 150000, 5, 20);
        return true;
    }

    @Override
    public ActiveType isActiveHere(Player player, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.level, pos, spot))
            return ActiveType.INACTIVE;
        if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.level, player.blockPosition(), OreSpawnEffect.NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.DIAMOND_ORE);
    }

    @Override
    public void update(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (level.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        var type = auraChunk.getType();
        Block requiredBlock;
        List<WeightedOre> ores;
        if (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)) {
            requiredBlock = Blocks.STONE;
            ores = NaturesAuraAPI.OVERWORLD_ORES;
        } else {
            requiredBlock = Blocks.NETHERRACK;
            ores = NaturesAuraAPI.NETHER_ORES;
        }
        var totalWeight = WeightedRandom.getTotalWeight(ores);

        var powders = NaturesAuraAPI.instance().getActiveEffectPowders(level,
                new AABB(pos).inflate(this.dist), OreSpawnEffect.NAME);
        if (powders.isEmpty())
            return;
        for (var i = 0; i < this.amount; i++) {
            var powder = powders.get(i % powders.size());
            var powderPos = powder.getA();
            int range = powder.getB();
            var x = Mth.floor(powderPos.x + level.random.nextGaussian() * range);
            var y = Mth.floor(powderPos.y + level.random.nextGaussian() * range);
            var z = Mth.floor(powderPos.z + level.random.nextGaussian() * range);
            var orePos = new BlockPos(x, y, z);
            if (orePos.distToCenterSqr(powderPos.x, powderPos.y, powderPos.z) <= range * range
                    && orePos.distSqr(pos) <= this.dist * this.dist && level.isLoaded(orePos)) {
                var state = level.getBlockState(orePos);
                if (state.getBlock() != requiredBlock)
                    continue;

                outer:
                while (true) {
                    var ore = WeightedRandom.getRandomItem(level.random, ores, totalWeight).orElse(null);
                    if (ore == null)
                        continue;
                    var tag = TagKey.create(Registry.BLOCK_REGISTRY, ore.tag);
                    if (tag == null)
                        continue;
                    for (var holder : Registry.BLOCK.getTagOrEmpty(tag)) {
                        var toPlace = holder.value();
                        if (toPlace == null || toPlace == Blocks.AIR)
                            continue;

                        var player = FakePlayerFactory.getMinecraft((ServerLevel) level);
                        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        var ray = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                        var context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, ray));
                        var stateToPlace = toPlace.getStateForPlacement(context);
                        if (OreSpawnEffect.SPAWN_EXCEPTIONS.contains(stateToPlace))
                            continue;

                        level.setBlockAndUpdate(orePos, stateToPlace);
                        level.levelEvent(2001, orePos, Block.getId(stateToPlace));

                        var toDrain = (20000 - ore.getWeight().asInt() * 2) * 2;
                        var highestSpot = IAuraChunk.getHighestSpot(level, orePos, 30, pos);
                        IAuraChunk.getAuraChunk(level, highestSpot).drainAura(highestSpot, toDrain);
                        break outer;
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(LevelChunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.oreEffect.get() &&
                (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return OreSpawnEffect.NAME;
    }
}
