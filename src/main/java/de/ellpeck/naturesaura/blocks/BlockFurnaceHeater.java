package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFurnaceHeater;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockFurnaceHeater extends BlockContainerImpl implements ICustomBlockState {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.makeCuboidShape(2, 12, 2, 14, 16, 14),    // Down
            Block.makeCuboidShape(2, 0, 2, 14, 4, 14),      // Up
            Block.makeCuboidShape(2, 2, 12, 14, 14, 16),    // North
            Block.makeCuboidShape(2, 2, 0, 14, 14, 4),      // South
            Block.makeCuboidShape(12, 2, 2, 16, 14, 14),    // West
            Block.makeCuboidShape(0, 2, 2, 4, 14, 14)       // East
    };

    public BlockFurnaceHeater() {
        super("furnace_heater", BlockEntityFurnaceHeater::new, Properties.create(Material.ROCK).hardnessAndResistance(3F).harvestLevel(1).harvestTool(ToolType.PICKAXE));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityFurnaceHeater && ((BlockEntityFurnaceHeater) tile).isActive) {
            Direction facing = stateIn.get(FACING);

            float x;
            float y;
            float z;
            if (facing == Direction.UP) {
                x = 0.35F + rand.nextFloat() * 0.3F;
                y = 0F;
                z = 0.35F + rand.nextFloat() * 0.3F;
            } else if (facing == Direction.DOWN) {
                x = 0.35F + rand.nextFloat() * 0.3F;
                y = 1F;
                z = 0.35F + rand.nextFloat() * 0.3F;
            } else {
                x = facing.getZOffset() != 0 ? (0.35F + rand.nextFloat() * 0.3F) : facing.getXOffset() < 0 ? 1 : 0;
                y = 0.35F + rand.nextFloat() * 0.3F;
                z = facing.getXOffset() != 0 ? (0.35F + rand.nextFloat() * 0.3F) : facing.getZOffset() < 0 ? 1 : 0;
            }

            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getXOffset(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getYOffset(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getZOffset(),
                    0xf46e42, rand.nextFloat() + 0.5F, 55, 0F, true, true);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(FACING).getIndex()];
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(FACING, context.getFace());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.directionalBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
