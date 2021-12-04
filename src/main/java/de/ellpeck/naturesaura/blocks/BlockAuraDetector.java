package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraDetector;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;

public class BlockAuraDetector extends BlockContainerImpl {

    public BlockAuraDetector() {
        super("aura_detector", BlockEntityAuraDetector::new, Properties.create(Material.ROCK).hardnessAndResistance(2F).sound(SoundType.STONE));
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, Level levelIn, BlockPos pos) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityAuraDetector)
            return ((BlockEntityAuraDetector) tile).redstonePower;
        else
            return 0;
    }
}
