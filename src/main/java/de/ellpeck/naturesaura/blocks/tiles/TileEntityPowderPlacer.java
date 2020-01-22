package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TileEntityPowderPlacer extends TileEntityImpl {

    public TileEntityPowderPlacer() {
        super(ModTileEntities.POWDER_PLACER);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0) {
            List<EntityEffectInhibitor> powders = this.world.getEntitiesWithinAABB(EntityEffectInhibitor.class,
                    new AxisAlignedBB(this.pos, this.pos.add(1, 2, 1)), EntityPredicates.IS_ALIVE);
            for (Direction facing : Direction.values()) {
                if (!facing.getAxis().isHorizontal())
                    continue;
                TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));
                if (tile == null)
                    continue;
                IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()).orElse(null);
                if (handler == null)
                    continue;

                if (!powders.isEmpty()) {
                    for (EntityEffectInhibitor powder : powders) {
                        ItemStack drop = powder.getDrop();
                        for (int i = 0; i < handler.getSlots(); i++) {
                            ItemStack remain = handler.insertItem(i, drop, false);
                            if (remain.isEmpty()) {
                                powder.remove();
                                break;
                            } else if (remain.getCount() != drop.getCount()) {
                                powder.setAmount(remain.getCount());
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.extractItem(i, Integer.MAX_VALUE, true);
                        if (stack.isEmpty() || stack.getItem() != ModItems.EFFECT_POWDER)
                            continue;
                        EntityEffectInhibitor.place(this.world, stack, this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5);
                        handler.extractItem(i, Integer.MAX_VALUE, false);
                        break;
                    }
                }
            }
        }
        super.onRedstonePowerChange(newPower);
    }
}
