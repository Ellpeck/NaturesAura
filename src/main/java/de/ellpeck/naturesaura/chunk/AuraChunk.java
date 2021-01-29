package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect.ActiveType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.misc.WorldData.WorldSection;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AuraChunk implements IAuraChunk {

    private final Chunk chunk;
    private final IAuraType type;
    private final Map<BlockPos, MutableInt> drainSpots = new ConcurrentHashMap<>();
    private final List<IDrainSpotEffect> effects = new ArrayList<>();
    private boolean needsSync;

    public AuraChunk(Chunk chunk, IAuraType type) {
        this.chunk = chunk;
        this.type = type;

        for (Supplier<IDrainSpotEffect> supplier : NaturesAuraAPI.DRAIN_SPOT_EFFECTS.values()) {
            IDrainSpotEffect effect = supplier.get();
            if (effect.appliesHere(this.chunk, this, this.type))
                this.effects.add(effect);
        }
    }

    @Override
    public int drainAura(BlockPos pos, int amount, boolean aimForZero, boolean simulate) {
        if (amount <= 0)
            return 0;
        MutableInt spot = this.getActualDrainSpot(pos, !simulate);
        int curr = spot != null ? spot.intValue() : 0;
        if (curr < 0 && curr - amount > 0) // Underflow protection
            return this.drainAura(pos.up(), amount, aimForZero, simulate);
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
        MutableInt spot = this.getActualDrainSpot(pos, !simulate);
        int curr = spot != null ? spot.intValue() : 0;
        if (curr > 0 && curr + amount < 0) // Overflow protection
            return this.storeAura(pos.up(), amount, aimForZero, simulate);
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
        MutableInt spot = this.drainSpots.get(pos);
        if (spot == null && make) {
            spot = new MutableInt();
            this.addDrainSpot(pos, spot);
        }
        return spot;
    }

    @Override
    public int getDrainSpot(BlockPos pos) {
        MutableInt spot = this.getActualDrainSpot(pos, false);
        return spot == null ? 0 : spot.intValue();
    }

    private void addDrainSpot(BlockPos pos, MutableInt spot) {
        int expX = pos.getX() >> 4;
        int expZ = pos.getZ() >> 4;
        ChunkPos myPos = this.getPos();
        if (expX != myPos.x || expZ != myPos.z)
            throw new IllegalArgumentException("Tried to add drain spot " + pos + " to chunk at " + myPos.x + ", " + myPos.z + " when it should've been added to chunk at " + expX + ", " + expZ);

        this.drainSpots.put(pos, spot);
    }

    public void setSpots(Map<BlockPos, MutableInt> spots) {
        this.drainSpots.clear();
        for (Map.Entry<BlockPos, MutableInt> entry : spots.entrySet())
            this.addDrainSpot(entry.getKey(), entry.getValue());
        this.addOrRemoveAsActive(this.drainSpots.size() > 0);
    }

    @Override
    public IAuraType getType() {
        return this.type;
    }

    @Override
    public void markDirty() {
        this.chunk.markDirty();
        this.needsSync = true;
        this.addOrRemoveAsActive(this.drainSpots.size() > 0);
    }

    public void update() {
        World world = this.chunk.getWorld();

        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos pos = entry.getKey();
            MutableInt amount = entry.getValue();
            for (IDrainSpotEffect effect : this.effects)
                effect.update(world, this.chunk, this, pos, amount.intValue());
        }

        if (this.needsSync) {
            ChunkPos pos = this.getPos();
            PacketHandler.sendToAllLoaded(world,
                    new BlockPos(pos.x * 16, 0, pos.z * 16),
                    this.makePacket());
            this.needsSync = false;
        }
    }

    public PacketAuraChunk makePacket() {
        ChunkPos pos = this.getPos();
        return new PacketAuraChunk(pos.x, pos.z, this.drainSpots);
    }

    public void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos drainPos = entry.getKey();
            if (drainPos.distanceSq(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue().intValue());
            }
        }
    }

    public void getActiveEffectIcons(PlayerEntity player, Map<ResourceLocation, Tuple<ItemStack, Boolean>> icons) {
        for (IDrainSpotEffect effect : this.effects) {
            Tuple<ItemStack, Boolean> alreadyThere = icons.get(effect.getName());
            if (alreadyThere != null && alreadyThere.getB())
                continue;
            for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
                BlockPos pos = entry.getKey();
                MutableInt amount = entry.getValue();
                ActiveType state = effect.isActiveHere(player, this.chunk, this, pos, amount.intValue());
                if (state == ActiveType.INACTIVE)
                    continue;
                ItemStack stack = effect.getDisplayIcon();
                if (stack.isEmpty())
                    continue;
                icons.put(effect.getName(), new Tuple<>(stack, state == ActiveType.INHIBITED));
            }
        }
    }

    public ChunkPos getPos() {
        return this.chunk.getPos();
    }

    public void addOrRemoveAsActive(boolean add) {
        ChunkPos chunkPos = this.getPos();
        long sectionPos = new ChunkPos(chunkPos.x >> WorldSection.B_SIZE - 4, chunkPos.z >> WorldSection.B_SIZE - 4).asLong();
        WorldData data = (WorldData) IWorldData.getWorldData(this.chunk.getWorld());
        if (add) {
            WorldSection section = data.worldSectionsWithSpots.computeIfAbsent(sectionPos, l -> new WorldSection());
            section.chunksWithSpots.put(chunkPos.asLong(), this);
        } else {
            WorldSection section = data.worldSectionsWithSpots.get(sectionPos);
            if (section != null) {
                section.chunksWithSpots.remove(chunkPos.asLong());
                if (section.chunksWithSpots.size() <= 0)
                    data.worldSectionsWithSpots.remove(sectionPos);
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        ListNBT list = new ListNBT();
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putLong("pos", entry.getKey().toLong());
            tag.putInt("amount", entry.getValue().intValue());
            list.add(tag);
        }

        CompoundNBT compound = new CompoundNBT();
        compound.put("drain_spots", list);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        this.drainSpots.clear();
        ListNBT list = compound.getList("drain_spots", 10);
        for (INBT base : list) {
            CompoundNBT tag = (CompoundNBT) base;
            this.addDrainSpot(
                    BlockPos.fromLong(tag.getLong("pos")),
                    new MutableInt(tag.getInt("amount")));
        }
        this.addOrRemoveAsActive(this.drainSpots.size() > 0);
    }

}
