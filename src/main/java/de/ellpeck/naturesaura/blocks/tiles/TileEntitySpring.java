package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlockEntitySpring extends BlockEntityImpl implements ITickableBlockEntity {

    private final LazyOptional<IFluidHandler> tank = LazyOptional.of(InfiniteTank::new);
    private AABBTicket waterTicket;

    public BlockEntitySpring() {
        super(ModTileEntities.SPRING);
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.level.isClientSide) {
            // add a ticket to water crops
            AxisAlignedBB area = new AxisAlignedBB(this.worldPosition).grow(5, 1, 5);
            this.waterTicket = FarmlandWaterManager.addAABBTicket(this.level, area);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (!this.level.isClientSide && this.waterTicket != null && this.waterTicket.isValid()) {
            this.waterTicket.invalidate();
            this.waterTicket = null;
        }
        this.tank.invalidate();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide || this.level.getGameTime() % 35 != 0)
            return;

        // fill cauldrons
        BlockPos up = this.worldPosition.up();
        BlockState upState = this.level.getBlockState(up);
        if (upState.hasProperty(CauldronBlock.LEVEL)) {
            int level = upState.get(CauldronBlock.LEVEL);
            if (level < 3) {
                this.level.setBlockState(up, upState.with(CauldronBlock.LEVEL, level + 1));
                this.level.playSound(null, up, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1, 1);
                this.consumeAura(2500);
                return;
            }
        }

        // wet sponges
        int spongeRadius = 2;
        for (int x = -spongeRadius; x <= spongeRadius; x++) {
            for (int y = -spongeRadius; y <= spongeRadius; y++) {
                for (int z = -spongeRadius; z <= spongeRadius; z++) {
                    BlockPos pos = this.worldPosition.add(x, y, z);
                    BlockState state = this.level.getBlockState(pos);
                    if (state.getBlock() == Blocks.SPONGE) {
                        this.level.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
                        this.level.playEvent(2001, pos, Block.getStateId(Blocks.WATER.getDefaultState()));
                        this.consumeAura(2500);
                        return;
                    }
                }
            }
        }

        // generate obsidian
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = this.worldPosition.offset(dir);
            if (this.isLava(side, true)) {
                this.level.setBlockState(side, ForgeEventFactory.fireFluidPlaceBlockEvent(this.level, side, side, Blocks.OBSIDIAN.getDefaultState()));
                this.level.playEvent(1501, side, 0);
                this.consumeAura(1500);
                return;
            }
        }

        // generate stone
        BlockPos twoUp = this.worldPosition.up(2);
        if (this.isLava(twoUp, false) && (this.level.getBlockState(up).isAir(this.level, up) || this.isLava(up, false))) {
            this.level.setBlockState(up, ForgeEventFactory.fireFluidPlaceBlockEvent(this.level, up, twoUp, Blocks.STONE.getDefaultState()));
            this.level.playEvent(1501, up, 0);
            this.consumeAura(150);
            return;
        }

        // generate cobblestone
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos twoSide = this.worldPosition.offset(dir, 2);
            BlockPos side = this.worldPosition.offset(dir);
            if (this.isLava(twoSide, false) && (this.level.getBlockState(side).isAir(this.level, side) || this.isLava(side, false))) {
                this.level.setBlockState(side, ForgeEventFactory.fireFluidPlaceBlockEvent(this.level, side, twoSide, Blocks.COBBLESTONE.getDefaultState()));
                this.level.playEvent(1501, side, 0);
                this.consumeAura(100);
                return;
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this.tank.cast();
        return LazyOptional.empty();
    }

    public void consumeAura(int amount) {
        while (amount > 0) {
            BlockPos pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 35, this.worldPosition);
            amount -= IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, amount);
        }
    }

    private boolean isLava(BlockPos offset, boolean source) {
        FluidState state = this.level.getFluidState(offset);
        return (!source || state.isSource()) && state.getFluid().isIn(FluidTags.LAVA);
    }

    private class InfiniteTank implements IFluidTank, IFluidHandler {
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
                BlockEntitySpring.this.consumeAura(MathHelper.ceil(drain / 2F));
            return new FluidStack(Fluids.WATER, drain);
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (this.isFluidValid(resource))
                return this.drain(resource.getAmount(), action);
            return FluidStack.EMPTY;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return this.getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return this.isFluidValid(stack);
        }
    }
}
