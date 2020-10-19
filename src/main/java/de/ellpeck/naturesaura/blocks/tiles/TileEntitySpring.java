package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileEntitySpring extends TileEntityImpl implements ITickableTileEntity {

    private final IFluidTank tank = new InfiniteTank();
    private AABBTicket waterTicket;

    public TileEntitySpring() {
        super(ModTileEntities.SPRING);
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.world.isRemote) {
            // add a ticket to water crops
            AxisAlignedBB area = new AxisAlignedBB(this.pos).grow(5, 1, 5);
            this.waterTicket = FarmlandWaterManager.addAABBTicket(this.world, area);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (!this.world.isRemote && this.waterTicket != null && this.waterTicket.isValid()) {
            this.waterTicket.invalidate();
            this.waterTicket = null;
        }
    }

    @Override
    public void tick() {
        if (this.world.isRemote || this.world.getGameTime() % 35 != 0)
            return;

        // fill cauldrons
        BlockPos up = this.pos.up();
        BlockState upState = this.world.getBlockState(up);
        if (upState.hasProperty(CauldronBlock.LEVEL)) {
            int level = upState.get(CauldronBlock.LEVEL);
            if (level < 3) {
                this.world.setBlockState(up, upState.with(CauldronBlock.LEVEL, level + 1));
                this.world.playSound(null, up, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1, 1);
                this.consumeAura(2500);
                return;
            }
        }

        // wet sponges
        int spongeRadius = 2;
        for (int x = -spongeRadius; x <= spongeRadius; x++) {
            for (int y = -spongeRadius; y <= spongeRadius; y++) {
                for (int z = -spongeRadius; z <= spongeRadius; z++) {
                    BlockPos pos = this.pos.add(x, y, z);
                    BlockState state = this.world.getBlockState(pos);
                    if (state.getBlock() == Blocks.SPONGE) {
                        this.world.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
                        this.world.playEvent(2001, pos, Block.getStateId(Blocks.WATER.getDefaultState()));
                        this.consumeAura(2500);
                        return;
                    }
                }
            }
        }

        // generate obsidian
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = this.pos.offset(dir);
            if (this.isLava(side, true)) {
                this.world.setBlockState(side, ForgeEventFactory.fireFluidPlaceBlockEvent(this.world, side, side, Blocks.OBSIDIAN.getDefaultState()));
                this.world.playEvent(1501, side, 0);
                this.consumeAura(1500);
                return;
            }
        }

        // generate stone
        BlockPos twoUp = this.pos.up(2);
        if (this.isLava(twoUp, false) && (this.world.getBlockState(up).isAir(this.world, up) || this.isLava(up, false))) {
            this.world.setBlockState(up, ForgeEventFactory.fireFluidPlaceBlockEvent(this.world, up, twoUp, Blocks.STONE.getDefaultState()));
            this.world.playEvent(1501, up, 0);
            this.consumeAura(150);
            return;
        }

        // generate cobblestone
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos twoSide = this.pos.offset(dir, 2);
            BlockPos side = this.pos.offset(dir);
            if (this.isLava(twoSide, false) && (this.world.getBlockState(side).isAir(this.world, side) || this.isLava(side, false))) {
                this.world.setBlockState(side, ForgeEventFactory.fireFluidPlaceBlockEvent(this.world, side, twoSide, Blocks.COBBLESTONE.getDefaultState()));
                this.world.playEvent(1501, side, 0);
                this.consumeAura(100);
                return;
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> (T) this.tank);
        return LazyOptional.empty();
    }

    public void consumeAura(int amount) {
        while (amount > 0) {
            BlockPos pos = IAuraChunk.getHighestSpot(this.world, this.pos, 35, this.pos);
            amount -= IAuraChunk.getAuraChunk(this.world, pos).drainAura(pos, amount);
        }
    }

    private boolean isLava(BlockPos offset, boolean source) {
        FluidState state = this.world.getFluidState(offset);
        return (!source || state.isSource()) && state.getFluid().isIn(FluidTags.LAVA);
    }

    private class InfiniteTank implements IFluidTank {
        @Override
        public FluidStack getFluid() {
            return new FluidStack(Fluids.WATER, 1000);
        }

        @Override
        public int getFluidAmount() {
            return 1000;
        }

        @Override
        public int getCapacity() {
            return 1000;
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isIn(FluidTags.WATER);
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            int drain = Math.min(maxDrain, 1000);
            if (action.execute())
                TileEntitySpring.this.consumeAura(2 * drain);
            return new FluidStack(Fluids.WATER, drain);
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (this.isFluidValid(resource))
                return this.drain(resource.getAmount(), action);
            return FluidStack.EMPTY;
        }
    }
}
