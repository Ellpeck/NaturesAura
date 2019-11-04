package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.gen.WorldGenAncientTree;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Random;

// Make this extend SaplingBlock?
public class BlockAncientSapling extends BushBlock implements IGrowable, IModItem, IModelProvider {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(
            0.09999999403953552D, 0.0D, 0.09999999403953552D,
            0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    public BlockAncientSapling() {
        super(ModBlocks.prop(Material.PLANTS).hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        ModRegistry.add(this);
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
        if (!world.isRemote) {
            super.updateTick(world, pos, state, rand);

            if (world.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(world, rand, pos, state);
            }
        }
    }

    @Override
    public String getBaseName() {
        return "ancient_sapling";
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SaplingBlock.STAGE, meta);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(SaplingBlock.STAGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SaplingBlock.STAGE);
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        return world.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, BlockState state) {
        if (state.getValue(SaplingBlock.STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(SaplingBlock.STAGE), 4);
        } else if (TerrainGen.saplingGrowTree(world, rand, pos)) {
            new WorldGenAncientTree(true).generate(world, rand, pos);
        }
    }
}