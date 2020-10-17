package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityPotionGenerator;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockPotionGenerator extends BlockContainerImpl implements ICustomBlockState {
    public BlockPotionGenerator() {
        super("potion_generator", TileEntityPotionGenerator::new, Properties.create(Material.ROCK).hardnessAndResistance(5F).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
