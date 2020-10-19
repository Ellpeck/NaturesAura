package de.ellpeck.naturesaura.reg;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModTileType<T extends TileEntity> implements IModItem {

    public final TileEntityType<T> type;
    public final String name;

    public ModTileType(Supplier<T> supplier, IModItem item) {
        this(supplier, item.getBaseName(), item);
    }

    public ModTileType(Supplier<T> supplier, String name, IModItem... items) {
        this.name = name;
        Block[] blocks = Arrays.stream(items).map(i -> (Block) i).toArray(Block[]::new);
        this.type = TileEntityType.Builder.create(supplier, blocks).build(null);
    }

    @Override
    public String getBaseName() {
        return this.name;
    }

    @Override
    public ForgeRegistryEntry getRegistryEntry() {
        return this.type;
    }
}
