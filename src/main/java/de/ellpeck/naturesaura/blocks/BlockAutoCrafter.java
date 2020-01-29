package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntityAutoCrafter;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;

public class BlockAutoCrafter extends BlockContainerImpl implements ICustomBlockState {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockAutoCrafter() {
        super("auto_crafter", TileEntityAutoCrafter::new, ModBlocks.prop(Material.WOOD).hardnessAndResistance(1.5F).sound(SoundType.WOOD));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.horizontalBlock(this, generator.models().cubeBottomTop(this.getBaseName(),
                generator.modLoc("block/" + this.getBaseName()),
                generator.modLoc("block/" + this.getBaseName() + "_bottom"),
                generator.modLoc("block/" + this.getBaseName() + "_top")));
    }
}
