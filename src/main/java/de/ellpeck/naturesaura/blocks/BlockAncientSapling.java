package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.gen.WorldGenAncientTree;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class BlockAncientSapling extends BlockBush implements IGrowable, IModItem, IModelProvider {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(
            0.09999999403953552D, 0.0D, 0.09999999403953552D,
            0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    public BlockAncientSapling() {
        this.setHardness(0.0F);
        this.setSoundType(SoundType.PLANT);

        ModRegistry.addItemOrBlock(this);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
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
    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockSapling.STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockSapling.STAGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSapling.STAGE);
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return world.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        if (state.getValue(BlockSapling.STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(BlockSapling.STAGE), 4);
        } else if (TerrainGen.saplingGrowTree(world, rand, pos)) {
            new WorldGenAncientTree(true).generate(world, rand, pos);
        }
    }
}