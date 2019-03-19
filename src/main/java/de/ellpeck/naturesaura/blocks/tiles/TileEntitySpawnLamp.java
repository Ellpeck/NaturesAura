package de.ellpeck.naturesaura.blocks.tiles;

public class TileEntitySpawnLamp extends TileEntityImpl {

    public int getRadius() {
        return this.redstonePower * 3;
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.world.isRemote)
            this.sendToClients();
    }
}
