package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityPlacer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPlacer extends BlockContainerImpl implements IVisualizable {

    public BlockPlacer() {
        super("placer", TileEntityPlacer::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(5);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x078a93;
    }
}
