package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class BlockStairsNA extends StairBlock implements IModItem, ICustomBlockState {

    public final String textureName;
    private final String baseName;

    public BlockStairsNA(String baseName, String textureName, Supplier<BlockState> modelState, Block.Properties properties) {
        super(modelState, properties.dynamicShape());
        this.baseName = baseName;
        this.textureName = textureName;
        ModRegistry.ALL_ITEMS.add(this);
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
