package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.misc.WorldData;

public class TileEntitySpawnLamp extends TileEntityImpl {

    public TileEntitySpawnLamp() {
        super(ModTileEntities.SPAWN_LAMP);
    }

    public int getRadius() {
        return this.redstonePower * 3;
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.world.isRemote) {
            WorldData data = (WorldData) IWorldData.getWorldData(this.world);
            data.spawnLamps.add(this);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (!this.world.isRemote) {
            WorldData data = (WorldData) IWorldData.getWorldData(this.world);
            data.spawnLamps.remove(this);
        }
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.world.isRemote)
            this.sendToClients();
    }
}
