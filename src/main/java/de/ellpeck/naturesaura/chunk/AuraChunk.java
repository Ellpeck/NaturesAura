package de.ellpeck.naturesaura.chunk;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect.ActiveType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class AuraChunk implements IAuraChunk {

    private final Map<BlockPos, DrainSpot> drainSpots = new ConcurrentHashMap<>();
    private final Table<BlockPos, Integer, Pair<Integer, Integer>> auraAndSpotAmountCache = HashBasedTable.create();
    private final Table<BlockPos, Integer, Pair<BlockPos, Integer>[]> limitSpotCache = HashBasedTable.create();
    private final List<IDrainSpotEffect> effects = new ArrayList<>();

    private LevelChunk chunk;
    private IAuraType type;
    private boolean needsSync;

    @Override
    public void ensureInitialized(LevelChunk chunk) {
        // are we already initialized?
        if (this.chunk != null)
            return;

        this.chunk = chunk;
        this.type = IAuraType.forLevel(chunk.getLevel());

        for (var supplier : NaturesAuraAPI.DRAIN_SPOT_EFFECTS.values()) {
            var effect = supplier.get();
            if (effect.appliesHere(this.chunk, this, this.type))
                this.effects.add(effect);
        }
    }

    @Override
    public int drainAura(BlockPos pos, int amount, boolean aimForZero, boolean simulate) {
        if (amount <= 0)
            return 0;
        var spot = this.getActualDrainSpot(pos, !simulate);
        var curr = spot != null ? spot.intValue() : 0;
        if (curr < 0 && curr - amount > 0) // Underflow protection
            return this.drainAura(pos.above(), amount, aimForZero, simulate);
        if (aimForZero) {
            if (curr > 0 && curr - amount < 0)
                amount = curr;
        }
        if (!simulate) {
            spot.subtract(amount);
            if (spot.intValue() == 0)
                this.drainSpots.remove(pos);
            this.markDirty();
        }
        return amount;
    }

    @Override
    public int drainAura(BlockPos pos, int amount) {
        return this.drainAura(pos, amount, false, false);
    }

    @Override
    public int storeAura(BlockPos pos, int amount, boolean aimForZero, boolean simulate) {
        if (amount <= 0)
            return 0;
        var spot = this.getActualDrainSpot(pos, !simulate);
        var curr = spot != null ? spot.intValue() : 0;
        if (curr > 0 && curr + amount < 0) // Overflow protection
            return this.storeAura(pos.above(), amount, aimForZero, simulate);
        if (aimForZero) {
            if (curr < 0 && curr + amount > 0) {
                amount = -curr;
            }
        }
        if (!simulate) {
            spot.add(amount);
            if (spot.intValue() == 0)
                this.drainSpots.remove(pos);
            this.markDirty();
        }
        return amount;
    }

    @Override
    public int storeAura(BlockPos pos, int amount) {
        return this.storeAura(pos, amount, true, false);
    }

    @Override
    public DrainSpot getActualDrainSpot(BlockPos pos, boolean make) {
        var spot = this.drainSpots.get(pos);
        if (spot == null && make) {
            spot = new DrainSpot(pos, 0);
            this.addDrainSpot(spot);
        }
        return spot;
    }

    @Override
    public int getDrainSpot(BlockPos pos) {
        var spot = this.getActualDrainSpot(pos, false);
        return spot == null ? 0 : spot.intValue();
    }

    private void addDrainSpot(DrainSpot spot) {
        var expX = spot.pos.getX() >> 4;
        var expZ = spot.pos.getZ() >> 4;
        var myPos = this.chunk.getPos();
        if (expX != myPos.x || expZ != myPos.z)
            throw new IllegalArgumentException("Tried to add drain spot " + spot.pos + " to chunk at " + myPos.x + ", " + myPos.z + " when it should've been added to chunk at " + expX + ", " + expZ);

        this.drainSpots.put(spot.pos, spot);
    }

    public void setSpots(Collection<DrainSpot> spots) {
        this.drainSpots.clear();
        for (var spot : spots)
            this.addDrainSpot(spot);
        this.addOrRemoveAsActive();
    }

    @Override
    public IAuraType getType() {
        return this.type;
    }

    @Override
    public void markDirty() {
        this.chunk.setUnsaved(true);
        this.needsSync = true;
        this.auraAndSpotAmountCache.clear();
        this.limitSpotCache.clear();
        this.addOrRemoveAsActive();
    }

    public void update() {
        var level = this.chunk.getLevel();

        for (var spot : this.drainSpots.values()) {
            for (var effect : this.effects)
                effect.update(level, this.chunk, this, spot.pos, spot.intValue(), spot);

            // cause this spot to fizzle out if it's over the range limit
            if (spot.intValue() > 0 && spot.originalSpreadPos != null && !spot.originalSpreadPos.closerThan(spot.pos, ModConfig.instance.maxAuraSpreadRange.get()))
                this.drainAura(spot.pos, spot.intValue());
        }

        if (this.needsSync) {
            var pos = this.chunk.getPos();
            PacketHandler.sendToAllLoaded(level,
                    new BlockPos(pos.x * 16, 0, pos.z * 16),
                    this.makePacket());
            this.needsSync = false;
        }
    }

    public PacketAuraChunk makePacket() {
        var pos = this.chunk.getPos();
        return new PacketAuraChunk(pos.x, pos.z, this.drainSpots.values());
    }

    public void getSpots(BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        for (var entry : this.drainSpots.entrySet()) {
            var drainPos = entry.getKey();
            if (drainPos.distSqr(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue().intValue());
            }
        }
    }

    public Pair<Integer, Integer> getAuraAndSpotAmount(BlockPos pos, int radius) {
        var ret = this.auraAndSpotAmountCache.get(pos, radius);
        if (ret == null) {
            var aura = new MutableInt();
            var spots = new MutableInt();
            this.getSpots(pos, radius, (p, i) -> {
                aura.add(i);
                spots.increment();
            });
            ret = Pair.of(aura.intValue(), spots.intValue());
            this.auraAndSpotAmountCache.put(pos, radius, ret);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public Pair<BlockPos, Integer>[] getLowestAndHighestSpots(BlockPos pos, int radius) {
        var ret = this.limitSpotCache.get(pos, radius);
        if (ret == null) {
            var lowestSpot = new MutableObject<BlockPos>();
            var highestSpot = new MutableObject<BlockPos>();
            var lowestAmount = new MutableInt(Integer.MAX_VALUE);
            var highestAmount = new MutableInt(Integer.MIN_VALUE);
            this.getSpots(pos, radius, (p, i) -> {
                if (i > highestAmount.intValue()) {
                    highestAmount.setValue(i);
                    highestSpot.setValue(p);
                }
                if (i < lowestAmount.intValue()) {
                    lowestAmount.setValue(i);
                    lowestSpot.setValue(p);
                }
            });
            ret = new Pair[]{
                    Pair.of(lowestSpot.getValue(), lowestAmount.intValue()),
                    Pair.of(highestSpot.getValue(), highestAmount.intValue())};
            this.limitSpotCache.put(pos, radius, ret);
        }
        return ret;
    }

    public void getActiveEffectIcons(Player player, Map<ResourceLocation, Tuple<ItemStack, Boolean>> icons) {
        for (var effect : this.effects) {
            var alreadyThere = icons.get(effect.getName());
            if (alreadyThere != null && alreadyThere.getB())
                continue;
            for (var entry : this.drainSpots.entrySet()) {
                var pos = entry.getKey();
                var amount = entry.getValue();
                var state = effect.isActiveHere(player, this.chunk, this, pos, amount.intValue());
                if (state == ActiveType.INACTIVE)
                    continue;
                var stack = effect.getDisplayIcon();
                if (stack.isEmpty())
                    continue;
                icons.put(effect.getName(), new Tuple<>(stack, state == ActiveType.INHIBITED));
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var list = new ListTag();
        for (var spot : this.drainSpots.values())
            list.add(spot.serializeNBT());
        var compound = new CompoundTag();
        compound.put("drain_spots", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        this.drainSpots.clear();
        var list = compound.getList("drain_spots", 10);
        for (var base : list)
            this.addDrainSpot(new DrainSpot((CompoundTag) base));
        this.addOrRemoveAsActive();
    }

    private void addOrRemoveAsActive() {
        var chunkPos = this.chunk.getPos().toLong();
        var data = (LevelData) ILevelData.getLevelData(this.chunk.getLevel());
        if (!this.drainSpots.isEmpty()) {
            data.auraChunksWithSpots.put(chunkPos, this);
        } else {
            data.auraChunksWithSpots.remove(chunkPos);
        }
    }

    public static class DrainSpot extends MutableInt {

        public final BlockPos pos;
        public BlockPos originalSpreadPos;

        public DrainSpot(BlockPos pos, int value) {
            super(value);
            this.pos = pos;
        }

        public DrainSpot(CompoundTag tag) {
            this(BlockPos.of(tag.getLong("pos")), tag.getInt("amount"));
            if (tag.contains("original_spread_pos"))
                this.originalSpreadPos = BlockPos.of(tag.getLong("original_spread_pos"));
        }

        public CompoundTag serializeNBT() {
            var ret = new CompoundTag();
            ret.putLong("pos", this.pos.asLong());
            ret.putInt("amount", this.intValue());
            if (this.originalSpreadPos != null)
                ret.putLong("original_spread_pos", this.originalSpreadPos.asLong());
            return ret;
        }

    }

}
