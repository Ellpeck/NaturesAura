package de.ellpeck.naturesaura.aura.chunk;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NACapabilities;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.aura.chunk.effect.GrassDieEffect;
import de.ellpeck.naturesaura.aura.chunk.effect.PlantBoostEffect;
import de.ellpeck.naturesaura.aura.chunk.effect.ReplenishingEffect;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AuraChunk implements IAuraChunk {

    private final Chunk chunk;
    private final IAuraType type;
    private final Map<BlockPos, MutableInt> drainSpots = new HashMap<>();
    private final List<IDrainSpotEffect> effects = new ArrayList<>();
    private boolean needsSync;

    public AuraChunk(Chunk chunk, IAuraType type) {
        this.chunk = chunk;
        this.type = type;

        this.addEffect(new ReplenishingEffect());
        this.addEffect(new GrassDieEffect());
        this.addEffect(new PlantBoostEffect());
    }

    @Override
    public void addEffect(IDrainSpotEffect effect) {
        if (effect.appliesToType(this.type))
            this.effects.add(effect);
    }

    @Override
    public void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos drainPos = entry.getKey();
            if (drainPos.distanceSq(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue());
            }
        }
    }

    @Override
    public void drainAura(BlockPos pos, int amount) {
        MutableInt spot = this.getDrainSpot(pos);
        spot.subtract(amount);
        if (spot.intValue() == 0)
            this.drainSpots.remove(pos);
        this.markDirty();
    }

    @Override
    public void storeAura(BlockPos pos, int amount) {
        MutableInt spot = this.getDrainSpot(pos);
        spot.add(amount);
        if (spot.intValue() == 0)
            this.drainSpots.remove(pos);
        this.markDirty();
    }

    @Override
    public MutableInt getDrainSpot(BlockPos pos) {
        MutableInt spot = this.drainSpots.get(pos);
        if (spot == null) {
            spot = new MutableInt();
            this.drainSpots.put(pos, spot);
        }
        return spot;
    }

    public void setSpots(Map<BlockPos, MutableInt> spots) {
        this.drainSpots.clear();
        this.drainSpots.putAll(spots);
    }

    @Override
    public IAuraType getType() {
        return this.type;
    }

    @Override
    public void markDirty() {
        this.needsSync = true;
    }

    public void update() {
        World world = this.chunk.getWorld();
        if (this.needsSync) {
            PacketHandler.sendToAllLoaded(world,
                    new BlockPos(this.chunk.x * 16, 0, this.chunk.z * 16),
                    this.makePacket());
            this.needsSync = false;
        }

        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            for (IDrainSpotEffect effect : this.effects) {
                world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":" + effect.getClass().getSimpleName());
                effect.update(world, this.chunk, this, entry.getKey(), entry.getValue());
                world.profiler.endSection();
            }
        }
    }

    public IMessage makePacket() {
        return new PacketAuraChunk(this.chunk.x, this.chunk.z, this.drainSpots);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == NACapabilities.auraChunk;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == NACapabilities.auraChunk ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("pos", entry.getKey().toLong());
            tag.setInteger("amount", entry.getValue().intValue());
            list.appendTag(tag);
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("drain_spots", list);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.drainSpots.clear();
        NBTTagList list = compound.getTagList("drain_spots", 10);
        for (NBTBase base : list) {
            NBTTagCompound tag = (NBTTagCompound) base;
            this.drainSpots.put(
                    BlockPos.fromLong(tag.getLong("pos")),
                    new MutableInt(tag.getInteger("amount")));
        }
    }
}
