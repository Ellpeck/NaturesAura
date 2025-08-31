package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.blocks.tiles.ModBlockEntities;
import de.ellpeck.naturesaura.blocks.tiles.render.RenderWoodStand;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.reg.IAxeBreakable;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;

public class BlockWoodStand extends BlockContainerImpl implements ITESRProvider<BlockEntityWoodStand>, ICustomBlockState, IAxeBreakable {

    private static final VoxelShape SHAPE = Shapes.create(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super("wood_stand", BlockEntityWoodStand.class, Properties.of().strength(1.5F).sound(SoundType.WOOD));
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockWoodStand.SHAPE;
    }

    @SubscribeEvent
    public void onTreeGrow(BlockGrowFeatureEvent event) {
        var level = event.getLevel();
        var pos = event.getPos();
        if (!level.isClientSide() && level instanceof Level) {
            if (Multiblocks.TREE_RITUAL.isComplete((Level) level, pos)) {
                var saplingStack = new ItemStack(level.getBlockState(pos).getBlock());
                if (!saplingStack.isEmpty()) {
                    for (var recipe : ((Level) level).getRecipeManager().getRecipesFor(ModRecipes.TREE_RITUAL_TYPE, null, (Level) level)) {
                        if (recipe.value().saplingType.test(saplingStack)) {
                            List<Ingredient> required = new ArrayList<>(recipe.value().ingredients);
                            var toPick = new MutableObject<BlockEntityWoodStand>();

                            var fine = Multiblocks.TREE_RITUAL.forEach(pos, 'W', (tilePos, matcher) -> {
                                var tile = level.getBlockEntity(tilePos);
                                if (tile instanceof BlockEntityWoodStand stand) {
                                    var stack = stand.items.getStackInSlot(0);
                                    if (!stack.isEmpty()) {
                                        for (var i = required.size() - 1; i >= 0; i--) {
                                            var req = required.get(i);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return Helper.putStackOnTile(player, hand, pos, 0, true);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public void registerTESR() {
        BlockEntityRenderers.register(ModBlockEntities.WOOD_STAND, RenderWoodStand::new);
    }

}
