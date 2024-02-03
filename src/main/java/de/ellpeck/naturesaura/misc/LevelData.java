package de.ellpeck.naturesaura.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySpawnLamp;
import de.ellpeck.naturesaura.blocks.tiles.ItemStackHandlerNA;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LevelData implements ILevelData {

    public final ListMultimap<ResourceLocation, Tuple<Vec3, Integer>> effectPowders = ArrayListMultimap.create();
    public final Long2ObjectOpenHashMap<AuraChunk> auraChunksWithSpots = new Long2ObjectOpenHashMap<>();
    public final List<BlockPos> recentlyConvertedMossStones = new ArrayList<>();
    public final Set<BlockEntitySpawnLamp> spawnLamps = new HashSet<>();
    private final Map<String, ItemStackHandlerNA> enderStorages = new HashMap<>();
    private final LazyOptional<LevelData> lazyThis = LazyOptional.of(() -> this);

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.CAP_LEVEL_DATA ? this.lazyThis.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();

        var storages = new ListTag();
        for (var entry : this.enderStorages.entrySet()) {
            var handler = entry.getValue();
            if (Helper.isEmpty(handler))
                continue;
            var storageComp = handler.serializeNBT();
            storageComp.putString("name", entry.getKey());
            storages.add(storageComp);
        }
        compound.put("storages", storages);

        var moss = new ListTag();
        for (var pos : this.recentlyConvertedMossStones)
            moss.add(LongTag.valueOf(pos.asLong()));
        compound.put("converted_moss", moss);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        this.enderStorages.clear();
        for (var base : compound.getList("storages", 10)) {
            var storageComp = (CompoundTag) base;
            var storage = this.getEnderStorage(storageComp.getString("name"));
            storage.deserializeNBT(storageComp);
        }

        this.recentlyConvertedMossStones.clear();
        for (var base : compound.getList("converted_moss", Tag.TAG_LONG))
            this.recentlyConvertedMossStones.add(BlockPos.of(((LongTag) base).getAsLong()));
    }

    @Override
    public ItemStackHandlerNA getEnderStorage(String name) {
        return this.enderStorages.computeIfAbsent(name, n -> new ItemStackHandlerNA(27));
    }

    @Override
    public boolean isEnderStorageLocked(String name) {
        var handler = this.getEnderStorage(name);
        for (var i = 0; i < handler.getSlots(); i++) {
            var stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.TOKEN_TERROR)
                return true;
        }
        return false;
    }

    public void addMossStone(BlockPos pos) {
        this.recentlyConvertedMossStones.add(pos);
        if (this.recentlyConvertedMossStones.size() > 512)
            this.recentlyConvertedMossStones.remove(0);
    }
}
