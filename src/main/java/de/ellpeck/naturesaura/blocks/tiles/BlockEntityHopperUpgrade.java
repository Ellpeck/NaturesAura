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
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BlockEntityHopperUpgrade extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityHopperUpgrade(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOPPER_UPGRADE, pos, state);
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
            var tile = this.level.getBlockEntity(this.worldPosition.below());
            if (!BlockEntityHopperUpgrade.isValidHopper(tile))
                return;
            var handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
            if (handler == null)
                return;

            var items = this.level.getEntitiesOfClass(ItemEntity.class, new AABB(this.worldPosition).inflate(7));
            if (items.isEmpty())
                return;

            var drainPerItem = 500;
            if (!this.canUseRightNow(drainPerItem * items.size()))
                return;

            for (var item : items) {
                if (!item.isAlive() || item.hasPickUpDelay())
                    continue;
                var stack = item.getItem();
                if (stack.isEmpty())
                    continue;
                var copy = stack.copy();

                for (var i = 0; i < handler.getSlots(); i++) {
                    copy = handler.insertItem(i, copy, false);
                    if (copy.isEmpty()) {
                        break;
                    }
                }

                if (!ItemStack.matches(stack, copy)) {
                    item.setItem(copy);
                    if (copy.isEmpty())
                        item.kill();

                    var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 25, this.worldPosition);
                    IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, drainPerItem);

                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.HOPPER_UPGRADE));
                }
            }
        }
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }
}
