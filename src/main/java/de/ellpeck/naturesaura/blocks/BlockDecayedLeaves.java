package de.ellpeck.naturesaura.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockDecayedLeaves extends BlockImpl {

    public BlockDecayedLeaves() {
        super("decayed_leaves", ModBlocks.prop(Material.LEAVES).hardnessAndResistance(0.2F).sound(SoundType.PLANT).variableOpacity().tickRandomly());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isRemote) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
