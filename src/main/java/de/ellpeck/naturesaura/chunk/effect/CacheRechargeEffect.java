package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CacheRechargeEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "cache_recharge");

    private int amount;
    private AxisAlignedBB bb;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot < 100000)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(world, pos, 20);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        int dist = MathHelper.clamp(aura / 3500, 3, 15);
        this.bb = new AxisAlignedBB(pos).grow(dist);
        this.amount = MathHelper.ceil(aura / 250F / auraAndSpots.getRight());
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return ActiveType.INACTIVE;
        if (!this.bb.contains(player.getPositionVec()))
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.world, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModItems.AURA_CACHE);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(world, pos, spot))
            return;
        List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, this.bb);
        for (PlayerEntity player : players) {
            if (NaturesAuraAPI.instance().isEffectPowderActive(world, player.getPosition(), NAME))
                continue;
            if (NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, true)) {
                NaturesAuraAPI.instance().insertAuraIntoPlayer(player, this.amount, false);
                auraChunk.drainAura(pos, this.amount);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.cacheRechargeEffect.get();
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
