package de.ellpeck.naturesaura.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockDecayedLeaves extends BlockImpl {

    public BlockDecayedLeaves() {
        super("decayed_leaves", Material.LEAVES);
        this.setTickRandomly(true);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, BlockState state, Random random) {
        if (!worldIn.isRemote) {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Items.AIR;
    }
}
