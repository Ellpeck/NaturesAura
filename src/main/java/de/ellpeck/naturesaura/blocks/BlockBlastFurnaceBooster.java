package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityBlastFurnaceBooster;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBlastFurnaceBooster extends BlockContainerImpl implements ICustomBlockState, IPickaxeBreakable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Shapes.create(1 / 16F, 0, 1 / 16F, 15 / 16F, 1, 15 / 16F);

    public BlockBlastFurnaceBooster() {
        super("blast_furnace_booster", BlockEntityBlastFurnaceBooster.class, Block.Properties.ofFullCopy(Blocks.BLAST_FURNACE).lightLevel(s -> 0));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockBlastFurnaceBooster.SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockBlastFurnaceBooster.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockBlastFurnaceBooster.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.horizontalBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
