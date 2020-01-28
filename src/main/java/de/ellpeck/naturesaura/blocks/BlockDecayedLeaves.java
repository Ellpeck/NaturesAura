package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockDecayedLeaves extends BlockImpl implements ICustomBlockState {

    public BlockDecayedLeaves() {
        super("decayed_leaves", ModBlocks.prop(Material.LEAVES).hardnessAndResistance(0.2F).sound(SoundType.PLANT).variableOpacity().notSolid().tickRandomly());
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isRemote) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1;
    }
}
