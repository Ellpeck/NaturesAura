package de.ellpeck.naturesaura.reg;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Arrays;

public class ModTileType<T extends BlockEntity> implements IModItem {

    public final BlockEntityType<T> type;
    public final String name;

    public ModTileType(BlockEntityType.BlockEntitySupplier<T> supplier, IModItem item) {
        this(supplier, item.getBaseName(), item);
    }

    public ModTileType(BlockEntityType.BlockEntitySupplier<T> supplier, String name, IModItem... items) {
        this.name = name;
        var blocks = Arrays.stream(items).map(i -> (Block) i).toArray(Block[]::new);
        this.type = BlockEntityType.Builder.of(supplier, blocks).build(null);
    }

    @Override
    public String getBaseName() {
        return this.name;
    }

    @Override
    public ForgeRegistryEntry<?> getRegistryEntry() {
        return this.type;
    }
}
