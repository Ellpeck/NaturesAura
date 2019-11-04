package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import net.minecraft.block.SlabBlock;

public class Slab extends SlabBlock implements IModItem, IModelProvider {

    private final String baseName;

    public Slab(String baseName, Properties properties) {
        super(properties);
        this.baseName = baseName;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
