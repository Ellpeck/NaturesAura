package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;

public class BlockEntitySpawnLamp extends BlockEntityImpl {

    public BlockEntitySpawnLamp() {
        super(ModTileEntities.SPAWN_LAMP);
    }

    public int getRadius() {
        return this.redstonePower * 3;
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.level.isClientSide) {
            LevelData data = (LevelData) ILevelData.getLevelData(this.level);
            data.spawnLamps.add(this);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (!this.level.isClientSide) {
            LevelData data = (LevelData) ILevelData.getLevelData(this.level);
            data.spawnLamps.remove(this);
        }
    }

    @Override
    public void onRedstonePowerChange(int newPower) {
        super.onRedstonePowerChange(newPower);
        if (!this.level.isClientSide)
            this.sendToClients();
    }
}
