package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFurnaceHeater;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.IPickaxeBreakable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockFurnaceHeater extends BlockContainerImpl implements ICustomBlockState, IPickaxeBreakable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape[] SHAPES = {
            Block.box(2, 12, 2, 14, 16, 14),    // Down
            Block.box(2, 0, 2, 14, 4, 14),      // Up
            Block.box(2, 2, 12, 14, 14, 16),    // North
            Block.box(2, 2, 0, 14, 14, 4),      // South
            Block.box(12, 2, 2, 16, 14, 14),    // West
            Block.box(0, 2, 2, 4, 14, 14)       // East
    };

    public BlockFurnaceHeater() {
        super("furnace_heater", BlockEntityFurnaceHeater.class, Properties.of().strength(3F));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityFurnaceHeater heater && heater.isActive) {
            var facing = stateIn.getValue(BlockFurnaceHeater.FACING);

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
                x = facing.getStepZ() != 0 ? 0.35F + rand.nextFloat() * 0.3F : facing.getStepX() < 0 ? 1 : 0;
                y = 0.35F + rand.nextFloat() * 0.3F;
                z = facing.getStepX() != 0 ? 0.35F + rand.nextFloat() * 0.3F : facing.getStepZ() < 0 ? 1 : 0;
            }

            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getStepX(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getStepY(),
                    (rand.nextFloat() * 0.016F + 0.01F) * facing.getStepZ(),
                    0xf46e42, rand.nextFloat() + 0.5F, 55, 0F, true, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockFurnaceHeater.SHAPES[state.getValue(BlockFurnaceHeater.FACING).get3DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockFurnaceHeater.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockFurnaceHeater.FACING, context.getClickedFace());
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.directionalBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
