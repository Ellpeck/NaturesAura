package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraDetector;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAuraDetector extends BlockContainerImpl {

    public BlockAuraDetector() {
        super("aura_detector", ModTileEntities.AURA_DETECTOR, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityAuraDetector)
            return ((TileEntityAuraDetector) tile).redstonePower;
        else
            return 0;
    }
}
