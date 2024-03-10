package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.entities.EntityEffectInhibitor;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;

public class BlockEntityPowderPlacer extends BlockEntityImpl {

    public BlockEntityPowderPlacer(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWDER_PLACER, pos, state);
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        if (this.redstonePower <= 0 && newPower > 0) {
            var powders = this.level.getEntitiesOfClass(EntityEffectInhibitor.class, new AABB(Vec3.atCenterOf(this.worldPosition), Vec3.atCenterOf(this.worldPosition.offset(1, 2, 1))), Entity::isAlive);
            for (var facing : Direction.values()) {
                if (!facing.getAxis().isHorizontal())
                    continue;
                var tile = this.level.getBlockEntity(this.worldPosition.relative(facing));
                if (tile == null)
                    continue;
                var handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, facing.getOpposite());
                if (handler == null)
                    continue;

                if (!powders.isEmpty()) {
                    for (var powder : powders) {
                        var drop = powder.getDrop();
                        for (var i = 0; i < handler.getSlots(); i++) {
                            var remain = handler.insertItem(i, drop, false);
                            if (remain.isEmpty()) {
                                powder.kill();
                                break;
                            } else if (remain.getCount() != drop.getCount()) {
                                powder.setAmount(remain.getCount());
                            }
                        }
                    }
                } else {
                    for (var i = 0; i < handler.getSlots(); i++) {
                        var stack = handler.extractItem(i, Integer.MAX_VALUE, true);
                        if (stack.isEmpty() || stack.getItem() != ModItems.EFFECT_POWDER)
                            continue;
                        EntityEffectInhibitor.place(this.level, stack, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5);
                        handler.extractItem(i, Integer.MAX_VALUE, false);
                        break;
                    }
                }
            }
        }
        super.onRedstonePowerChange(newPower);
    }

}
