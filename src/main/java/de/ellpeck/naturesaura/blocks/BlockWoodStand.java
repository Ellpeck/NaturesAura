package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderWoodStand;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockWoodStand extends BlockContainerImpl implements ITESRProvider {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super("wood_stand", TileEntityWoodStand::new, ModBlocks.prop(Material.WOOD).hardnessAndResistance(1.5F).sound(SoundType.WOOD).harvestLevel(0).harvestTool(ToolType.AXE));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote()) {
            if (Multiblocks.TREE_RITUAL.isComplete(world, pos)) {
                BlockState sapling = world.getBlockState(pos);
                ItemStack saplingStack = sapling.getBlock().getItem(world, pos, sapling);
                if (!saplingStack.isEmpty()) {
                    for (TreeRitualRecipe recipe : NaturesAuraAPI.TREE_RITUAL_RECIPES.values()) {
                        if (recipe.saplingType.test(saplingStack)) {
                            List<Ingredient> required = new ArrayList<>(Arrays.asList(recipe.ingredients));
                            MutableObject<TileEntityWoodStand> toPick = new MutableObject<>();

                            boolean fine = Multiblocks.TREE_RITUAL.forEach(pos, 'W', (tilePos, matcher) -> {
                                TileEntity tile = world.getTileEntity(tilePos);
                                if (tile instanceof TileEntityWoodStand) {
                                    TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                                    ItemStack stack = stand.items.getStackInSlot(0);
                                    if (!stack.isEmpty()) {
                                        for (int i = required.size() - 1; i >= 0; i--) {
                                            Ingredient req = required.get(i);
                                            if (req.test(stack)) {
                                                required.remove(i);

                                                if (toPick.getValue() == null) {
                                                    toPick.setValue(stand);
                                                }
                                                return true;
                                            }
                                        }
                                        return false;
                                    }
                                }
                                return true;
                            });

                            if (fine && required.isEmpty()) {
                                toPick.getValue().setRitual(pos, recipe);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    /*

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
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public Tuple<Class, TileEntityRenderer> getTESR() {
        return new Tuple<>(TileEntityWoodStand.class, new RenderWoodStand());
    }
}
