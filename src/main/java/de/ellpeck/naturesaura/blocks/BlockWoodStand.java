package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderWoodStand;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer;
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.tileentity.BlockEntityType;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.Shapes;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevel;
import net.minecraft.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockWoodStand extends BlockContainerImpl implements ITESRProvider<BlockEntityWoodStand>, ICustomBlockState {

    private static final VoxelShape SHAPE = Shapes.create(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super("wood_stand", BlockEntityWoodStand::new, Properties.create(Material.WOOD).hardnessAndResistance(1.5F).sound(SoundType.WOOD).harvestLevel(0).harvestTool(ToolType.AXE));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        ILevel level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!level.isClientSide() && level instanceof Level) {
            if (Multiblocks.TREE_RITUAL.isComplete((Level) level, pos)) {
                BlockState sapling = level.getBlockState(pos);
                ItemStack saplingStack = sapling.getBlock().getItem(level, pos, sapling);
                if (!saplingStack.isEmpty()) {
                    for (TreeRitualRecipe recipe : ((Level) level).getRecipeManager().getRecipes(ModRecipes.TREE_RITUAL_TYPE, null, null)) {
                        if (recipe.saplingType.test(saplingStack)) {
                            List<Ingredient> required = new ArrayList<>(Arrays.asList(recipe.ingredients));
                            MutableObject<BlockEntityWoodStand> toPick = new MutableObject<>();

                            boolean fine = Multiblocks.TREE_RITUAL.forEach(pos, 'W', (tilePos, matcher) -> {
                                BlockEntity tile = level.getBlockEntity(tilePos);
                                if (tile instanceof BlockEntityWoodStand) {
                                    BlockEntityWoodStand stand = (BlockEntityWoodStand) tile;
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
    public InteractionResult onBlockActivated(BlockState state, Level levelIn, BlockPos pos, Player player, Hand handIn, BlockRayTraceResult hit) {
        return Helper.putStackOnTile(player, handIn, pos, 0, true);
    }

    @Override
    public Tuple<BlockEntityType<BlockEntityWoodStand>, Supplier<Function<? super BlockEntityRendererDispatcher, ? extends BlockEntityRenderer<? super BlockEntityWoodStand>>>> getTESR() {
        return new Tuple<>(ModTileEntities.WOOD_STAND, () -> RenderWoodStand::new);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
