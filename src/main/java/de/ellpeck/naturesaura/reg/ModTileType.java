package de.ellpeck.naturesaura.reg;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class ModTileType<T extends TileEntity> implements IModItem {

    public final TileEntityType<T> type;
    public final String name;

    public ModTileType(Supplier<T> supplier, IModItem block) {
        this.type = TileEntityType.Builder.create(supplier, (Block) block).build(null);
        this.name = block.getBaseName();
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
