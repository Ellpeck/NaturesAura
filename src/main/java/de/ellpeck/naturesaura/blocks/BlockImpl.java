package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;

public class BlockImpl extends Block implements IModItem, IModelProvider {

    private final String baseName;

    public BlockImpl(String baseName, Block.Properties properties) {
        super(properties);
        this.baseName = baseName;
        this.setRegistryName(NaturesAura.createRes(this.getBaseName()));
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
