package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockGoldenLeaves extends LeavesBlock implements
        IModItem, IModelProvider, IColorProvidingBlock, IColorProvidingItem {

    public static final int HIGHEST_STAGE = 3;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, HIGHEST_STAGE);

    public BlockGoldenLeaves() {
        super(ModBlocks.prop(Material.LEAVES, MaterialColor.GOLD).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT));
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return "golden_leaves";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(STAGE) == HIGHEST_STAGE && rand.nextFloat() >= 0.75F)
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + rand.nextFloat(),
                    0F, 0F, 0F,
                    0xF2FF00, 0.5F + rand.nextFloat(), 50, 0F, false, true);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(STAGE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> {
            int color = 0xF2FF00;
            if (state != null && worldIn != null && pos != null) {
                int foliage = BiomeColors.func_228361_b_(worldIn, pos);
                return Helper.blendColors(color, foliage, state.get(STAGE) / (float) HIGHEST_STAGE);
            } else {
                return color;
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xF2FF00;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (!worldIn.isRemote) {
            int stage = state.get(STAGE);
            if (stage < HIGHEST_STAGE) {
                worldIn.setBlockState(pos, state.with(STAGE, stage + 1));
            }

            if (stage > 1) {
                BlockPos offset = pos.offset(Direction.random(random));
                if (worldIn.isBlockLoaded(offset))
                    convert(worldIn, offset);
            }
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    public static boolean convert(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if ((state.getBlock().isFoliage(state, world, pos) || state.getBlock() instanceof LeavesBlock) && !(state.getBlock() instanceof BlockAncientLeaves || state.getBlock() instanceof BlockGoldenLeaves)) {
            if (!world.isRemote) {
                world.setBlockState(pos, ModBlocks.GOLDEN_LEAVES.getDefaultState()
                        .with(DISTANCE, state.has(DISTANCE) ? state.get(DISTANCE) : 1)
                        .with(PERSISTENT, state.has(PERSISTENT) ? state.get(PERSISTENT) : false));
            }
            return true;
        }
        return false;
    }
}