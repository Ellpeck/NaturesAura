package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityPlacer extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityPlacer(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLACER, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 15 == 0) {
            if (this.redstonePower > 0)
                return;
            var tileUp = this.level.getBlockEntity(this.worldPosition.above());
            if (tileUp == null)
                return;
            var handler = tileUp.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).orElse(null);
            if (handler == null)
                return;
            var frames = Helper.getAttachedItemFrames(this.level, this.worldPosition);
            if (frames.isEmpty())
                return;

            var toDrain = 1000;
            if (!this.canUseRightNow(toDrain))
                return;

            List<BlockPos> validPositions = new ArrayList<>();
            var range = 5;
            for (var x = -range; x <= range; x++)
                for (var y = -range; y <= range; y++)
                    for (var z = -range; z <= range; z++) {
                        var pos = this.worldPosition.offset(x, y, z);
                        if (!this.framesContain(frames, this.level.getBlockState(pos)))
                            continue;

                        var up = pos.above();
                        var state = this.level.getBlockState(up);
                        if (state.canBeReplaced())
                            validPositions.add(up);
                    }
            if (validPositions.isEmpty())
                return;

            for (var i = 0; i < handler.getSlots(); i++) {
                var stack = handler.extractItem(i, 1, true);
                if (stack.isEmpty())
                    continue;

                var pos = validPositions.get(this.level.random.nextInt(validPositions.size()));
                var left = this.tryPlace(stack.copy(), pos);
                if (ItemStack.matches(stack, left))
                    continue;

                handler.extractItem(i, 1, false);
                var spot = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 10, this.worldPosition);
                IAuraChunk.getAuraChunk(this.level, spot).drainAura(spot, toDrain);

                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), PacketParticles.Type.PLACER_PLACING));

                return;
            }
        }
    }

    @Override
    public boolean allowsLowerLimiter() {
        return true;
    }

    private boolean framesContain(List<ItemFrame> frames, BlockState state) {
        var stack = new ItemStack(state.getBlock());
        if (stack.isEmpty())
            return false;

        for (var frame : frames) {
            var frameStack = frame.getItem();
            if (frameStack.isEmpty())
                continue;
            if (Helper.areItemsEqual(stack, frameStack, false))
                return true;

            if (state.getBlock() == Blocks.FARMLAND && frameStack.getItem() == ModItems.FARMING_STENCIL)
                return true;
        }
        return false;
    }

    private ItemStack tryPlace(ItemStack stack, BlockPos pos) {
        if (!(this.level instanceof ServerLevel))
            return stack;
        var fake = FakePlayerFactory.getMinecraft((ServerLevel) this.level);
        fake.getInventory().items.set(fake.getInventory().selected, stack);
        var ray = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        ForgeHooks.onPlaceItemIntoWorld(new UseOnContext(fake, InteractionHand.MAIN_HAND, ray));
        return fake.getMainHandItem().copy();
    }
}
