package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderNatureAltar;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockNatureAltar extends BlockContainerImpl implements ITESRProvider<TileEntityNatureAltar>, ICustomBlockState {

    private static final VoxelShape SHAPE = VoxelShapes.create(0, 0, 0, 1, 12 / 16F, 1);
    public static final BooleanProperty NETHER = BooleanProperty.create("nether");

    public BlockNatureAltar() {
        super("nature_altar", TileEntityNatureAltar::new, Properties.create(Material.ROCK).hardnessAndResistance(4F).harvestLevel(1).harvestTool(ToolType.PICKAXE));
        this.setDefaultState(this.getDefaultState().with(NETHER, false));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public Tuple<TileEntityType<TileEntityNatureAltar>, Supplier<Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super TileEntityNatureAltar>>>> getTESR() {
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
        boolean nether = IAuraType.forWorld(context.getWorld()).isSimilar(NaturesAuraAPI.TYPE_NETHER);
        return super.getStateForPlacement(context).with(NETHER, nether);
    }
}
