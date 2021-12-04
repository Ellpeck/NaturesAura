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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.Player;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.BlockEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockNatureAltar extends BlockContainerImpl implements ITESRProvider<BlockEntityNatureAltar>, ICustomBlockState {

    private static final VoxelShape SHAPE = Shapes.create(0, 0, 0, 1, 12 / 16F, 1);
    public static final BooleanProperty NETHER = BooleanProperty.create("nether");

    public BlockNatureAltar() {
        super("nature_altar", BlockEntityNatureAltar::new, Block.Properties.create(Material.ROCK).hardnessAndResistance(4F).harvestLevel(1).harvestTool(ToolType.PICKAXE));
        this.setDefaultState(this.getDefaultState().with(NETHER, false));
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

    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand handIn, BlockRayTraceResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public Tuple<BlockEntityType<BlockEntityNatureAltar>, Supplier<Function<? super BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<? super BlockEntityNatureAltar>>>> getTESR() {
        return new Tuple<>(ModTileEntities.NATURE_ALTAR, () -> RenderNatureAltar::new);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        // noop
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NETHER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean nether = IAuraType.forLevel(context.getLevel()).isSimilar(NaturesAuraAPI.TYPE_NETHER);
        return super.getStateForPlacement(context).with(NETHER, nether);
    }
}
