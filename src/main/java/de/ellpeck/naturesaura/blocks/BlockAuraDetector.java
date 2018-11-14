package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraDetector;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAuraDetector extends BlockContainerImpl {

    public BlockAuraDetector() {
        super(Material.ROCK, "aura_detector", TileEntityAuraDetector.class, "aura_detector");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2F);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityAuraDetector)
            return ((TileEntityAuraDetector) tile).redstonePower;
        else
            return 0;
    }
}
