package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.packet.PacketAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
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
        MutableInt spot = this.getActualDrainSpot(pos, true);
        int curr = spot.intValue();
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
        MutableInt spot = this.getActualDrainSpot(pos, true);
        int curr = spot.intValue();
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
        if (expX != this.chunk.x || expZ != this.chunk.z)
            throw new IllegalArgumentException("Tried to add drain spot " + pos + " to chunk at " + this.chunk.x + ", " + this.chunk.z + " when it should've been added to chunk at " + expX + ", " + expZ);

        this.drainSpots.put(pos, spot);
    }

    public void setSpots(Map<BlockPos, MutableInt> spots) {
        this.drainSpots.clear();
        for (Map.Entry<BlockPos, MutableInt> entry : spots.entrySet())
            this.addDrainSpot(entry.getKey(), entry.getValue());
    }

    @Override
    public IAuraType getType() {
        return this.type;
    }

    @Override
    public void markDirty() {
        this.chunk.markDirty();
        this.needsSync = true;
    }

    public void update() {
        World world = this.chunk.getWorld();

        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos pos = entry.getKey();
            MutableInt amount = entry.getValue();
            for (IDrainSpotEffect effect : this.effects) {
                world.profiler.func_194340_a(() -> effect.getName().toString());
                effect.update(world, this.chunk, this, pos, amount.intValue());
                world.profiler.endSection();
            }
        }

        if (this.needsSync) {
            PacketHandler.sendToAllLoaded(world,
                    new BlockPos(this.chunk.x * 16, 0, this.chunk.z * 16),
                    this.makePacket());
            this.needsSync = false;
        }
    }

    public IMessage makePacket() {
        return new PacketAuraChunk(this.chunk.x, this.chunk.z, this.drainSpots);
    }

    public void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            BlockPos drainPos = entry.getKey();
            if (drainPos.distanceSq(pos) <= radius * radius) {
                consumer.accept(drainPos, entry.getValue().intValue());
            }
        }
    }

    public void getActiveEffectIcons(EntityPlayer player, Map<ResourceLocation, Tuple<ItemStack, Boolean>> icons) {
        for (IDrainSpotEffect effect : this.effects) {
            Tuple<ItemStack, Boolean> alreadyThere = icons.get(effect.getName());
            if (alreadyThere != null && alreadyThere.getSecond())
                continue;
            for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
                BlockPos pos = entry.getKey();
                MutableInt amount = entry.getValue();
                int state = effect.isActiveHere(player, this.chunk, this, pos, amount.intValue());
                if (state < 0)
                    continue;
                ItemStack stack = effect.getDisplayIcon();
                if (stack.isEmpty())
                    continue;
                icons.put(effect.getName(), new Tuple<>(stack, state == 0));
            }
        }
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
            this.addDrainSpot(
                    BlockPos.fromLong(tag.getLong("pos")),
                    new MutableInt(tag.getInteger("amount")));
        }
    }
}
