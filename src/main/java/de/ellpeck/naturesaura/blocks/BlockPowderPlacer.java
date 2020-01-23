package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityPowderPlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockPowderPlacer extends BlockContainerImpl {

    private static final VoxelShape SHAPE = VoxelShapes.create(0F, 0F, 0F, 1F, 4 / 16F, 1F);

    public BlockPowderPlacer() {
        super("powder_placer", TileEntityPowderPlacer::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2, 5F).sound(SoundType.STONE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
