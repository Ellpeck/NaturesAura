package de.ellpeck.naturesaura.aura.chunk;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AuraChunk implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    public static final int DEFAULT_AURA = 10000;

    private final Chunk chunk;
    private final Map<BlockPos, MutableInt> drainSpots = new HashMap<>();
    private boolean needsSync;

    public AuraChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public static void getSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                Chunk chunk = world.getChunk(x, z);
                if (chunk.hasCapability(Capabilities.auraChunk, null)) {
                    AuraChunk auraChunk = chunk.getCapability(Capabilities.auraChunk, null);
                    auraChunk.getSpotsInArea(pos, radius, consumer);
                }
            }
        }
    }

    public static int getAuraInArea(World world, BlockPos pos, int radius) {
        MutableInt result = new MutableInt(DEFAULT_AURA);
        getSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> result.add(drainSpot.intValue()));
        return result.intValue();
    }

    public static AuraChunk getAuraChunk(World world, BlockPos pos) {
        Chunk chunk = world.getChunk(pos);
        if (chunk.hasCapability(Capabilities.auraChunk, null)) {
            return chunk.getCapability(Capabilities.auraChunk, null);
        } else {
            return null;
        }
    }

    public static BlockPos getClosestSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        MutableDouble closestDist = new MutableDouble(Double.MAX_VALUE);
        MutableObject<BlockPos> closestSpot = new MutableObject<>();
        getSpotsInArea(world, pos, radius, (blockPos, drainSpot) -> {
            double dist = pos.distanceSq(blockPos);
            if (dist < radius * radius && dist < closestDist.doubleValue()) {
                closestDist.setValue(dist);
                closestSpot.setValue(blockPos);
            }
        });
        BlockPos closest = closestSpot.getValue();
        if (closest == null) {
            closest = defaultSpot;
        }
        return closest;
    }

    public void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos drainPos = entry.getKey();
            if (drainPos.distanceSq(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue());
            }
        }
    }

    public void drainAura(BlockPos pos, int amount) {
        MutableInt spot = this.getDrainSpot(pos);
        spot.subtract(amount);
        if (spot.intValue() == 0)
            this.drainSpots.remove(pos);
        this.markDirty();
    }

    public void storeAura(BlockPos pos, int amount) {
        MutableInt spot = this.getDrainSpot(pos);
        spot.add(amount);
        if (spot.intValue() == 0)
            this.drainSpots.remove(pos);
        this.markDirty();
    }

    private MutableInt getDrainSpot(BlockPos pos) {
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

        if (world.getTotalWorldTime() % 40 == 0) {
            for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
                BlockPos pos = entry.getKey();
                int amount = entry.getValue().intValue();
                if (amount < 0) {
                    List<TileEntity> tiles = new ArrayList<>();
                    Helper.getTileEntitiesInArea(world, pos, 25, tile -> {
                        if (tile.hasCapability(Capabilities.auraContainer, null)) {
                            IAuraContainer container = tile.getCapability(Capabilities.auraContainer, null);
                            if (container instanceof ISpotDrainable) {
                                tiles.add(tile);
                            }
                        }
                    });
                    if (!tiles.isEmpty()) {
                        for (int i = world.rand.nextInt(10) + 5; i >= 0; i--) {
                            TileEntity tile = tiles.get(world.rand.nextInt(tiles.size()));
                            IAuraContainer container = tile.getCapability(Capabilities.auraContainer, null);
                            int drained = ((ISpotDrainable) container).drainAuraPassively(-amount, false);
                            this.storeAura(pos, drained);
                            amount += drained;
                            if (amount >= drained) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public IMessage makePacket() {
        return new PacketAuraChunk(this.chunk.x, this.chunk.z, this.drainSpots);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.auraChunk;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.auraChunk ? (T) this : null;
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
