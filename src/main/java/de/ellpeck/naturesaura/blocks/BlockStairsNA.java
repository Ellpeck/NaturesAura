package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

import java.util.function.Supplier;

public class BlockStairsNA extends StairsBlock implements IModItem, ICustomBlockState {

    public final String textureName;
    private final String baseName;

    public BlockStairsNA(String baseName, String textureName, Supplier<BlockState> modelState, Block.Properties properties) {
        super(modelState, properties.variableOpacity());
        this.baseName = baseName;
        this.textureName = textureName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.stairsBlock(this, generator.modLoc("block/" + this.textureName));
    }
}
