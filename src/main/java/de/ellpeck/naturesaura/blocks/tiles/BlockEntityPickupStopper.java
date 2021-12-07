package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityPickupStopper extends BlockEntityImpl {

    public BlockEntityPickupStopper(BlockPos pos, BlockState state) {
        super(ModTileEntities.PICKUP_STOPPER, pos, state);
    }

    public float getRadius() {
        return this.redstonePower / 2F;
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.level.isClientSide)
            this.sendToClients();
    }
}
