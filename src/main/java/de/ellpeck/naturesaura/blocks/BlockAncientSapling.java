package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.gen.ModFeatures;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Random;
import java.util.function.Supplier;

public class BlockAncientSapling extends BushBlock implements BonemealableBlock, IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    protected static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockAncientSapling() {
        super(Properties.of(Material.GRASS).strength(0.0F).sound(SoundType.GRASS));
        ModRegistry.add(this);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!level.isClientSide) {
            super.randomTick(state, level, pos, random);

            if (level.getLightEmission(pos.above()) >= 9 && random.nextInt(7) == 0)
                this.performBonemeal(level, random, pos, state);
        }
    }

    @Override
    public String getBaseName() {
        return "ancient_sapling";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SaplingBlock.STAGE);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter p_50897_, BlockPos p_50898_, BlockState p_50899_, boolean p_50900_) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random rand, BlockPos pos, BlockState state) {
        return level.random.nextFloat() < 0.45F;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random rand, BlockPos pos, BlockState state) {
        if (state.getValue(SaplingBlock.STAGE) == 0) {
            level.setBlock(pos, state.cycle(SaplingBlock.STAGE), 4);
        } else if (ForgeEventFactory.saplingGrowTree(level, rand, pos)) {
            ModFeatures.Configured.ANCIENT_TREE.value().place(level, level.getChunkSource().getGenerator(), rand, pos);
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
        return RenderType::cutoutMipped;
    }
}