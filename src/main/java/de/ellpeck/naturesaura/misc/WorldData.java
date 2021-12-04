package de.ellpeck.naturesaura.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.ItemStackHandlerNA;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySpawnLamp;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LevelData implements ILevelData {

    public final ListMultimap<ResourceLocation, Tuple<Vector3d, Integer>> effectPowders = ArrayListMultimap.create();
    public final Long2ObjectOpenHashMap<AuraChunk> auraChunksWithSpots = new Long2ObjectOpenHashMap<>();
    public final List<BlockPos> recentlyConvertedMossStones = new ArrayList<>();
    public final Set<BlockEntitySpawnLamp> spawnLamps = new HashSet<>();
    private final Map<String, ItemStackHandlerNA> enderStorages = new HashMap<>();
    private final LazyOptional<LevelData> lazyThis = LazyOptional.of(() -> this);

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.capLevelData ? this.lazyThis.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        ListNBT storages = new ListNBT();
        for (Map.Entry<String, ItemStackHandlerNA> entry : this.enderStorages.entrySet()) {
            ItemStackHandlerNA handler = entry.getValue();
            if (Helper.isEmpty(handler))
                continue;
            CompoundTag storageComp = handler.serializeNBT();
            storageComp.putString("name", entry.getKey());
            storages.add(storageComp);
        }
        compound.put("storages", storages);

        ListNBT moss = new ListNBT();
        for (BlockPos pos : this.recentlyConvertedMossStones)
            moss.add(LongNBT.valueOf(pos.toLong()));
        compound.put("converted_moss", moss);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        this.enderStorages.clear();
        for (INBT base : compound.getList("storages", 10)) {
            CompoundTag storageComp = (CompoundTag) base;
            ItemStackHandlerNA storage = this.getEnderStorage(storageComp.getString("name"));
            storage.deserializeNBT(storageComp);
        }

        this.recentlyConvertedMossStones.clear();
        for (INBT base : compound.getList("converted_moss", Constants.NBT.TAG_LONG))
            this.recentlyConvertedMossStones.add(BlockPos.fromLong(((LongNBT) base).getLong()));
    }

    @Override
    public ItemStackHandlerNA getEnderStorage(String name) {
        return this.enderStorages.computeIfAbsent(name, n -> new ItemStackHandlerNA(27));
    }

    @Override
    public boolean isEnderStorageLocked(String name) {
        ItemStackHandlerNA handler = this.getEnderStorage(name);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
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
