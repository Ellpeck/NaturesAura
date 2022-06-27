package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraTimer;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderAuraTimer;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;
import java.util.function.Supplier;

public class BlockAuraTimer extends BlockContainerImpl implements ICustomBlockState, ITESRProvider<BlockEntityAuraTimer>, ICustomRenderType {

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);

    public BlockAuraTimer() {
        super("aura_timer", BlockEntityAuraTimer.class, Properties.copy(Blocks.SMOOTH_STONE));
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockAuraTimer.SHAPE;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::cutout;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED);
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }

    @Override
    public void tick(BlockState state, ServerLevel levelIn, BlockPos pos, RandomSource random) {
        super.tick(state, levelIn, pos, random);
        if (state.getValue(BlockStateProperties.POWERED))
            levelIn.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModBlockEntities.AURA_TIMER, RenderAuraTimer::new);
    }
}
