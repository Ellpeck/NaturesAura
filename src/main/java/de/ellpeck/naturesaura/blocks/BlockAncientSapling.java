package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.gen.WorldGenAncientTree;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;

public class BlockAncientSapling extends BushBlock implements IGrowable, IModItem, IModelProvider {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockAncientSapling() {
        super(ModBlocks.prop(Material.PLANTS).hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        ModRegistry.add(this);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isRemote) {
            super.randomTick(state, world, pos, random);

            if (world.getLight(pos.up()) >= 9 && random.nextInt(7) == 0) {
                this.grow(world, random, pos, state);
            }
        }
    }

    @Override
    public String getBaseName() {
        return "ancient_sapling";
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SaplingBlock.STAGE);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        return world.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        if (state.get(SaplingBlock.STAGE) == 0) {
            world.setBlockState(pos, state.cycle(SaplingBlock.STAGE), 4);
        } else if (ForgeEventFactory.saplingGrowTree(world, rand, pos)) {
            new WorldGenAncientTree(true).place(world, world.getChunkProvider().getChunkGenerator(), rand, pos, IFeatureConfig.NO_FEATURE_CONFIG);
        }
    }
}