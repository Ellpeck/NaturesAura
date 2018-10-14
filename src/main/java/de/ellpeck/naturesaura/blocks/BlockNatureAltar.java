package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockNatureAltar extends BlockContainerImpl {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(0F, 0F, 0F, 1F, 12 / 16F, 1F);

    public BlockNatureAltar() {
        super(Material.ROCK, "nature_altar", TileEntityNatureAltar.class, "nature_altar");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
