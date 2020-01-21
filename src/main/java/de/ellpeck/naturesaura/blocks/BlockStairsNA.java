package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

import java.util.function.Supplier;

public class BlockStairsNA extends StairsBlock implements IModItem, IModelProvider {

    private final String baseName;

    public BlockStairsNA(String baseName, Supplier<BlockState> modelState, Block.Properties properties) {
        super(modelState, properties.variableOpacity());
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
