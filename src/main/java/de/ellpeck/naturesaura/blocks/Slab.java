package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class Slab extends SlabBlock implements IModItem, ICustomBlockState {

    public final String textureName;
    private final String baseName;

    public Slab(String baseName, String textureName, Block.Properties properties) {
        super(properties);
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
        var texture = generator.modLoc("block/" + this.textureName);
        generator.models().cubeAll(this.getBaseName() + "_double", texture);
        generator.slabBlock(this, generator.modLoc(this.getBaseName() + "_double"), texture);
    }
}
