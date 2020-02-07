package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.ResourceLocation;

public class Slab extends SlabBlock implements IModItem, ICustomBlockState {

    public final String textureName;
    private final String baseName;

    public Slab(String baseName, String textureName, Properties properties) {
        super(properties);
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
        ResourceLocation texture = generator.modLoc("block/" + this.textureName);
        generator.models().cubeAll(this.getBaseName() + "_double", texture);
        generator.slabBlock(this, generator.modLoc(this.getBaseName() + "_double"), texture);
    }
}
