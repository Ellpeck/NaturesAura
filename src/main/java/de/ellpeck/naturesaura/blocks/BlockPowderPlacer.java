package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityPowderPlacer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPowderPlacer extends BlockContainerImpl {
    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(0F, 0F, 0F, 1F, 4 / 16F, 1F);

    public BlockPowderPlacer() {
        super(Material.ROCK, "powder_placer", TileEntityPowderPlacer.class, "powder_placer");
        this.setSoundType(SoundType.STONE);
        this.setHardness(2.5F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(BlockState baseState, IBlockAccess world, BlockPos pos, Direction side) {
        return side == Direction.DOWN;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
}
