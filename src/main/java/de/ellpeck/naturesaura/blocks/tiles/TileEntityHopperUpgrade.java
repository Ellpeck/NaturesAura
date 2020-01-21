package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TileEntityHopperUpgrade extends TileEntityImpl implements ITickableTileEntity {
    public TileEntityHopperUpgrade(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 10 == 0) {
            if (IAuraChunk.getAuraInArea(this.world, this.pos, 25) < 100000)
                return;
            TileEntity tile = this.world.getTileEntity(this.pos.down());
            if (!isValidHopper(tile))
                return;
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
            if (handler == null)
                return;

            List<ItemEntity> items = this.world.getEntitiesWithinAABB(ItemEntity.class,
                    new AxisAlignedBB(this.pos).grow(7));
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

                    BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 25, this.pos);
                    IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, 500);

                    // TODO particles
                   /* PacketHandler.sendToAllAround(this.world, this.pos, 32,
                            new PacketParticles((float) item.posX, (float) item.posY, (float) item.posZ, 10));*/
                }
            }
        }
    }

    private static boolean isValidHopper(TileEntity tile) {
        if (tile instanceof HopperTileEntity)
            return tile.getWorld().getBlockState(tile.getPos()).get(HopperBlock.ENABLED);
        if (tile instanceof TileEntityGratedChute)
            return ((TileEntityGratedChute) tile).redstonePower <= 0;
        return false;
    }
}
