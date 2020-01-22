package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityFireworkGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFireworkGenerator extends BlockContainerImpl implements IVisualizable {
    public BlockFireworkGenerator() {
        super("firework_generator", TileEntityFireworkGenerator::new, ModBlocks.prop(Material.ROCK).hardnessAndResistance(3F).sound(SoundType.STONE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(4);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xa442f4;
    }
}
