package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.blocks.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerrainGenEvents {

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote) {
            if (Multiblocks.TREE_RITUAL.forEachMatcher(world, pos, Rotation.NONE, (char) 0, (start, actionPos, x, y, z, ch, matcher) ->
                    ch == 'W' || Multiblocks.TREE_RITUAL.test(world, start, x, y, z, Rotation.NONE))) {
                IBlockState sapling = world.getBlockState(pos);
                ItemStack saplingStack = sapling.getBlock().getItem(world, pos, sapling);
                if (!saplingStack.isEmpty()) {
                    for (TreeRitualRecipe recipe : TreeRitualRecipe.RECIPES.values()) {
                        if (recipe.saplingType.isItemEqual(saplingStack)) {
                            List<ItemStack> required = new ArrayList<>(Arrays.asList(recipe.items));
                            MutableBoolean tooMuch = new MutableBoolean();
                            MutableObject<TileEntityWoodStand> toPick = new MutableObject<>();

                            Multiblocks.TREE_RITUAL.forEach(world, pos, Rotation.NONE, 'W', tilePos -> {
                                TileEntity tile = world.getTileEntity(tilePos);
                                if (tile instanceof TileEntityWoodStand) {
                                    TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                                    ItemStack stack = stand.items.getStackInSlot(0);
                                    if (!stack.isEmpty()) {
                                        int index = Helper.getItemIndex(required, stack);
                                        if (index >= 0) {
                                            required.remove(index);

                                            if (toPick.getValue() == null) {
                                                toPick.setValue(stand);
                                            }
                                        } else {
                                            tooMuch.setTrue();
                                        }
                                    }
                                }
                            });

                            if (tooMuch.isFalse() && required.isEmpty()) {
                                toPick.getValue().setRitual(pos, recipe);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
