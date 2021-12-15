package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityNatureAltar;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderNatureAltar;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockNatureAltar extends BlockContainerImpl implements ITESRProvider<BlockEntityNatureAltar>, ICustomBlockState {

    private static final VoxelShape SHAPE = Shapes.create(0, 0, 0, 1, 12 / 16F, 1);
    public static final BooleanProperty NETHER = BooleanProperty.create("nether");

    public BlockNatureAltar() {
        super("nature_altar", BlockEntityNatureAltar::new, Block.Properties.of(Material.STONE).strength(4F));
        this.registerDefaultState(this.defaultBlockState().setValue(NETHER, false));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override

    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        // noop
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NETHER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var nether = IAuraType.forLevel(context.getLevel()).isSimilar(NaturesAuraAPI.TYPE_NETHER);
        return super.getStateForPlacement(context).setValue(NETHER, nether);
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModTileEntities.NATURE_ALTAR, RenderNatureAltar::new);
    }
}
