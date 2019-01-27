package de.ellpeck.naturesaura.blocks.tiles;

public class TileEntityPickupStopper extends TileEntityImpl {

    public float getRadius() {
        return this.redstonePower / 2F;
    }

    @Override
    public void onRedstonePowerChange() {
        if (!this.world.isRemote)
            this.sendToClients();
    }
}
