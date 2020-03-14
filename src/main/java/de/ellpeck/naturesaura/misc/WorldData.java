package de.ellpeck.naturesaura.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.tiles.ItemStackHandlerNA;
import de.ellpeck.naturesaura.blocks.tiles.TileEntitySpawnLamp;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class WorldData implements IWorldData {
    public final ListMultimap<ResourceLocation, Tuple<Vec3d, Integer>> effectPowders = ArrayListMultimap.create();
    public final List<BlockPos> recentlyConvertedMossStones = new ArrayList<>();
    private final Map<String, ItemStackHandlerNA> enderStorages = new HashMap<>();
    public final Set<TileEntitySpawnLamp> spawnLamps = new HashSet<>();

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.capWorldData ? LazyOptional.of(() -> (T) this) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();

        ListNBT storages = new ListNBT();
        for (Map.Entry<String, ItemStackHandlerNA> entry : this.enderStorages.entrySet()) {
            ItemStackHandlerNA handler = entry.getValue();
            if (Helper.isEmpty(handler))
                continue;
            CompoundNBT storageComp = handler.serializeNBT();
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
    public void deserializeNBT(CompoundNBT compound) {
        this.enderStorages.clear();
        for (INBT base : compound.getList("storages", 10)) {
            CompoundNBT storageComp = (CompoundNBT) base;
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
        if (this.recentlyConvertedMossStones.size() > 2048)
            this.recentlyConvertedMossStones.remove(0);
    }
}
