package de.ellpeck.naturesaura.chunk;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class AuraChunk implements IAuraChunk {

    private final LevelChunk chunk;
    private final IAuraType type;
    private final Map<BlockPos, MutableInt> drainSpots = new ConcurrentHashMap<>();
    private final List<IDrainSpotEffect> effects = new ArrayList<>();
    private boolean needsSync;

    public AuraChunk(LevelChunk chunk, IAuraType type) {
        this.chunk = chunk;
        this.type = type;

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

    private MutableInt getActualDrainSpot(BlockPos pos, boolean make) {
        var spot = this.drainSpots.get(pos);
        if (spot == null && make) {
            spot = new MutableInt();
            this.addDrainSpot(pos, spot);
        }
        return spot;
    }

    @Override
    public int getDrainSpot(BlockPos pos) {
        var spot = this.getActualDrainSpot(pos, false);
        return spot == null ? 0 : spot.intValue();
    }

    private void addDrainSpot(BlockPos pos, MutableInt spot) {
        var expX = pos.getX() >> 4;
        var expZ = pos.getZ() >> 4;
        var myPos = this.chunk.getPos();
        if (expX != myPos.x || expZ != myPos.z)
            throw new IllegalArgumentException("Tried to add drain spot " + pos + " to chunk at " + myPos.x + ", " + myPos.z + " when it should've been added to chunk at " + expX + ", " + expZ);

        this.drainSpots.put(pos, spot);
    }

    public void setSpots(Map<BlockPos, MutableInt> spots) {
        this.drainSpots.clear();
        for (var entry : spots.entrySet())
            this.addDrainSpot(entry.getKey(), entry.getValue());
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
        this.addOrRemoveAsActive();
    }

    public void update() {
        var level = this.chunk.getLevel();

        for (var entry : this.drainSpots.entrySet()) {
            var pos = entry.getKey();
            var amount = entry.getValue();
            for (var effect : this.effects)
                effect.update(level, this.chunk, this, pos, amount.intValue());
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
        return new PacketAuraChunk(pos.x, pos.z, this.drainSpots);
    }

    public void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        for (var entry : this.drainSpots.entrySet()) {
            var drainPos = entry.getKey();
            if (drainPos.distSqr(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue().intValue());
            }
        }
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
        for (var entry : this.drainSpots.entrySet()) {
            var tag = new CompoundTag();
            tag.putLong("pos", entry.getKey().asLong());
            tag.putInt("amount", entry.getValue().intValue());
            list.add(tag);
        }

        var compound = new CompoundTag();
        compound.put("drain_spots", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        this.drainSpots.clear();
        var list = compound.getList("drain_spots", 10);
        for (var base : list) {
            var tag = (CompoundTag) base;
            this.addDrainSpot(
                    BlockPos.of(tag.getLong("pos")),
                    new MutableInt(tag.getInt("amount")));
        }
        this.addOrRemoveAsActive();
    }

    private void addOrRemoveAsActive() {
        var chunkPos = this.chunk.getPos().toLong();
        var data = (LevelData) ILevelData.getLevelData(this.chunk.getLevel());
        if (this.drainSpots.size() > 0) {
            data.auraChunksWithSpots.put(chunkPos, this);
        } else {
            data.auraChunksWithSpots.remove(chunkPos);
        }
    }
}
