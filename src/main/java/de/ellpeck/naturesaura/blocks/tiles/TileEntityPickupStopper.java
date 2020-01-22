package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.tileentity.TileEntityType;

public class TileEntityPickupStopper extends TileEntityImpl {

    public TileEntityPickupStopper() {
        super(ModTileEntities.PICKUP_STOPPER);
    }

    public float getRadius() {
        return this.redstonePower / 2F;
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.world.isRemote)
            this.sendToClients();
    }
}
