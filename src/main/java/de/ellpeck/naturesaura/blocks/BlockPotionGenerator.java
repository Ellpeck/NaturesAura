package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockPotionGenerator extends BlockContainerImpl {
    public BlockPotionGenerator() {
        super("potion_generator", ModTileEntities.POTION_GENERATOR, ModBlocks.prop(Material.ROCK).hardnessAndResistance(5F).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    }
}
