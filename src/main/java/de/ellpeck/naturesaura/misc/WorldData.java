package de.ellpeck.naturesaura.misc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.blocks.tiles.ItemStackHandlerNA;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WorldData implements IWorldData {
    private final Map<String, ItemStackHandlerNA> enderStorages = new HashMap<>();
    public final ListMultimap<ResourceLocation, Tuple<Vec3d, Integer>> effectPowders = ArrayListMultimap.create();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.capWorldData;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == NaturesAuraAPI.capWorldData ? (T) this : null;
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
            storageComp.setString("name", entry.getKey());
            storages.appendTag(storageComp);
        }
        compound.setTag("storages", storages);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        this.enderStorages.clear();
        ListNBT storages = compound.getTagList("storages", 10);
        for (NBTBase base : storages) {
            CompoundNBT storageComp = (CompoundNBT) base;
            ItemStackHandlerNA storage = this.getEnderStorage(storageComp.getString("name"));
            storage.deserializeNBT(storageComp);
        }
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
}
