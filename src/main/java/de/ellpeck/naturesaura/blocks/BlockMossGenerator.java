package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityMossGenerator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMossGenerator extends BlockContainerImpl implements IVisualizable {
    public BlockMossGenerator() {
        super("moss_generator", TileEntityMossGenerator.class, "moss_generator", ModBlocks.prop(Material.ROCK).hardnessAndResistance(2.5F).sound(SoundType.STONE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(2);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x15702d;
    }
}
