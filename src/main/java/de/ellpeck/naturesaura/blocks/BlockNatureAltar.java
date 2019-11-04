package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityNatureAltar;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderNatureAltar;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class BlockNatureAltar extends BlockContainerImpl implements ITESRProvider {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(0F, 0F, 0F, 1F, 12 / 16F, 1F);

    public BlockNatureAltar() {
        super("nature_altar", TileEntityNatureAltar.class, "nature_altar", ModBlocks.prop(Material.ROCK).hardnessAndResistance(4F).harvestLevel(1).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        return Helper.putStackOnTile(playerIn, hand, pos, 0, true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(BlockState baseState, IBlockAccess world, BlockPos pos, Direction side) {
        return side == Direction.DOWN;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityNatureAltar.class, new RenderNatureAltar());
    }
}
