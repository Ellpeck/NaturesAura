package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class TileEntityPlacer extends TileEntityImpl implements ITickableTileEntity {

    public TileEntityPlacer() {
        super(ModTileEntities.PLACER);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 15 == 0) {
            if (this.redstonePower > 0)
                return;
            TileEntity tileUp = this.world.getTileEntity(this.pos.up());
            if (tileUp == null)
                return;
            IItemHandler handler = tileUp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
            if (handler == null)
                return;
            List<ItemFrameEntity> frames = Helper.getAttachedItemFrames(this.world, this.pos);
            if (frames.isEmpty())
                return;

            List<BlockPos> validPositions = new ArrayList<>();
            int range = 5;
            for (int x = -range; x <= range; x++)
                for (int y = -range; y <= range; y++)
                    for (int z = -range; z <= range; z++) {
                        BlockPos pos = this.pos.add(x, y, z);
                        if (!this.framesContain(frames, pos, this.world.getBlockState(pos)))
                            continue;

                        BlockPos up = pos.up();
                        BlockState state = this.world.getBlockState(up);
                        if (state.getMaterial().isReplaceable())
                            validPositions.add(up);
                    }
            if (validPositions.isEmpty())
                return;

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.extractItem(i, 1, true);
                if (stack.isEmpty())
                    continue;

                BlockPos pos = validPositions.get(this.world.rand.nextInt(validPositions.size()));
                ItemStack left = this.tryPlace(stack.copy(), pos);
                if (ItemStack.areItemStacksEqual(stack, left))
                    continue;

                handler.extractItem(i, 1, false);
                BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 10, this.pos);
                IAuraChunk.getAuraChunk(this.world, spot).drainAura(spot, 1000);

                PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(pos.getX(), pos.getY(), pos.getZ(), 9));

                return;
            }
        }
    }

    private boolean framesContain(List<ItemFrameEntity> frames, BlockPos pos, BlockState state) {
        ItemStack stack = state.getBlock().getItem(this.world, pos, state);
        if (stack.isEmpty())
            return false;

        for (ItemFrameEntity frame : frames) {
            ItemStack frameStack = frame.getDisplayedItem();
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
        if (!(this.world instanceof ServerWorld))
            return stack;
        FakePlayer fake = FakePlayerFactory.getMinecraft((ServerWorld) this.world);
        fake.inventory.mainInventory.set(fake.inventory.currentItem, stack);
        BlockRayTraceResult ray = new BlockRayTraceResult(new Vec3d(pos).add(0.5F, 0.5F, 0.5F), Direction.UP, pos, false);
        ForgeHooks.onPlaceItemIntoWorld(new ItemUseContext(fake, Hand.MAIN_HAND, ray));
        return fake.getHeldItemMainhand().copy();
    }
}
