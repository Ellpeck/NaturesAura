package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperBlockEntity;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class BlockEntityHopperUpgrade extends BlockEntityImpl implements ITickableBlockEntity {
    public BlockEntityHopperUpgrade() {
        super(ModTileEntities.HOPPER_UPGRADE);
    }

    private static boolean isValidHopper(BlockEntity tile) {
        if (tile instanceof HopperBlockEntity)
            return tile.getLevel().getBlockState(tile.getPos()).get(HopperBlock.ENABLED);
        if (tile instanceof BlockEntityGratedChute)
            return ((BlockEntityGratedChute) tile).redstonePower <= 0;
        return false;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            if (IAuraChunk.getAuraInArea(this.level, this.worldPosition, 25) < 100000)
                return;
            BlockEntity tile = this.level.getBlockEntity(this.worldPosition.down());
            if (!isValidHopper(tile))
                return;
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
            if (handler == null)
                return;

            List<ItemEntity> items = this.level.getEntitiesWithinAABB(ItemEntity.class,
                    new AxisAlignedBB(this.worldPosition).grow(7));
            if (items.isEmpty())
                return;

            for (ItemEntity item : items) {
                if (!item.isAlive() || item.cannotPickup())
                    continue;
                ItemStack stack = item.getItem();
                if (stack.isEmpty())
                    continue;
                ItemStack copy = stack.copy();

                for (int i = 0; i < handler.getSlots(); i++) {
                    copy = handler.insertItem(i, copy, false);
                    if (copy.isEmpty()) {
                        break;
                    }
                }

                if (!ItemStack.areItemStacksEqual(stack, copy)) {
                    item.setItem(copy);
                    if (copy.isEmpty())
                        item.remove();

                    BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 25, this.worldPosition);
                    IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, 500);

                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticles((float) item.getPosX(), (float) item.getPosY(), (float) item.getPosZ(), PacketParticles.Type.HOPPER_UPGRADE));
                }
            }
        }
    }
}
