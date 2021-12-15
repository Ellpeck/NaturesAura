package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class BlockEntityHopperUpgrade extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityHopperUpgrade(BlockPos pos, BlockState state) {
        super(ModTileEntities.HOPPER_UPGRADE, pos, state);
    }

    private static boolean isValidHopper(BlockEntity tile) {
        if (tile instanceof HopperBlockEntity)
            return tile.getLevel().getBlockState(tile.getBlockPos()).getValue(HopperBlock.ENABLED);
        if (tile instanceof BlockEntityGratedChute)
            return ((BlockEntityGratedChute) tile).redstonePower <= 0;
        return false;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 10 == 0) {
            if (IAuraChunk.getAuraInArea(this.level, this.worldPosition, 25) < 100000)
                return;
            BlockEntity tile = this.level.getBlockEntity(this.worldPosition.below());
            if (!isValidHopper(tile))
                return;
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
            if (handler == null)
                return;

            List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.worldPosition).inflate(7));
            if (items.isEmpty())
                return;

            for (ItemEntity item : items) {
                if (!item.isAlive() || item.hasPickUpDelay())
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

                if (!ItemStack.isSame(stack, copy)) {
                    item.setItem(copy);
                    if (copy.isEmpty())
                        item.kill();

                    BlockPos spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 25, this.worldPosition);
                    IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, 500);

                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.HOPPER_UPGRADE));
                }
            }
        }
    }
}
