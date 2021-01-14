package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAuraTimer;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderAuraTimer;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockAuraTimer extends BlockContainerImpl implements ICustomBlockState, ITESRProvider<TileEntityAuraTimer>, ICustomRenderType {

    private static final VoxelShape SHAPE = makeCuboidShape(1, 0, 1, 15, 15, 15);

    public BlockAuraTimer() {
        super("aura_timer", TileEntityAuraTimer::new, Properties.from(Blocks.SMOOTH_STONE));
        this.setDefaultState(this.getDefaultState().with(BlockStateProperties.POWERED, false));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<TileEntityType<TileEntityAuraTimer>, Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super TileEntityAuraTimer>>>> getTESR() {
        return new Tuple<>(ModTileEntities.AURA_TIMER, () -> RenderAuraTimer::new);
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutout;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return state.get(BlockStateProperties.POWERED) ? 15 : 0;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.tick(state, worldIn, pos, random);
        if (state.get(BlockStateProperties.POWERED))
            worldIn.setBlockState(pos, state.with(BlockStateProperties.POWERED, false));
    }

}
