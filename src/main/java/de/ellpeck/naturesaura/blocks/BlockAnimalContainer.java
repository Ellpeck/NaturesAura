package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalContainer;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockAnimalContainer extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 13, 11);

    public BlockAnimalContainer() {
        super("animal_container", BlockEntityAnimalContainer.class, Properties.copy(Blocks.STONE));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockAnimalContainer.SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        var tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityAnimalContainer) {
            var radius = ((BlockEntityAnimalContainer) tile).getRadius();
            if (radius > 0)
                return new AABB(pos).inflate(radius);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0x42ddf5;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
