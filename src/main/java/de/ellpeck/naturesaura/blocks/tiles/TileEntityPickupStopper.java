package de.ellpeck.naturesaura.blocks.tiles;

public class BlockEntityPickupStopper extends BlockEntityImpl {

    public BlockEntityPickupStopper() {
        super(ModTileEntities.PICKUP_STOPPER);
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
