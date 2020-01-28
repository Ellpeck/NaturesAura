package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderWoodStand;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BlockWoodStand extends BlockContainerImpl implements ITESRProvider {

    private static final VoxelShape SHAPE = VoxelShapes.create(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super("wood_stand", TileEntityWoodStand::new, ModBlocks.prop(Material.WOOD).hardnessAndResistance(1.5F).sound(SoundType.WOOD).harvestLevel(0).harvestTool(ToolType.AXE));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public Tuple<TileEntityType, Function<TileEntityRendererDispatcher, TileEntityRenderer<? extends TileEntity>>> getTESR() {
        return new Tuple<>(ModTileEntities.WOOD_STAND, RenderWoodStand::new);
    }
}
