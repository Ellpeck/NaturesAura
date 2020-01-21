package de.ellpeck.naturesaura.blocks.tiles;

import net.minecraft.tileentity.TileEntityType;

public class TileEntitySpawnLamp extends TileEntityImpl {

    public TileEntitySpawnLamp(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

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
