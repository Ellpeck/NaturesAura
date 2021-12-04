package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.server.ServerLevel;

import java.util.Random;
import java.util.function.Supplier;

public class BlockDecayedLeaves extends BlockImpl implements ICustomBlockState, ICustomRenderType {

    public BlockDecayedLeaves() {
        super("decayed_leaves", Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).sound(SoundType.PLANT).notSolid().tickRandomly());
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!level.isClientSide) {
            level.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader levelIn, BlockPos pos) {
        return 1;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
    }
}
