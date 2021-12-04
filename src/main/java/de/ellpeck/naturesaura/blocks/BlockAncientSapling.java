package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.gen.ModFeatures;
import de.ellpeck.naturesaura.gen.LevelGenAncientTree;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraft.level.server.ServerLevel;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

public class BlockAncientSapling extends BushBlock implements IGrowable, IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockAncientSapling() {
        super(Properties.create(Material.PLANTS).hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        ModRegistry.add(this);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!level.isClientSide) {
            super.randomTick(state, level, pos, random);

            if (level.getLight(pos.up()) >= 9 && random.nextInt(7) == 0) {
                this.grow(level, random, pos, state);
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
    public boolean canGrow(IBlockReader levelIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(Level level, Random rand, BlockPos pos, BlockState state) {
        return level.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(ServerLevel level, Random rand, BlockPos pos, BlockState state) {
        if (state.get(SaplingBlock.STAGE) == 0) {
            level.setBlockState(pos, state.func_235896_a_(SaplingBlock.STAGE), 4);
        } else if (ForgeEventFactory.saplingGrowTree(level, rand, pos)) {
            ModFeatures.ANCIENT_TREE.func_241855_a(level, level.getChunkProvider().getChunkGenerator(), rand, pos, LevelGenAncientTree.CONFIG);
        }
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cross(this.getBaseName(), generator.modLoc("block/" + this.getBaseName())));
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "block/" + this.getBaseName());
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
    }
}