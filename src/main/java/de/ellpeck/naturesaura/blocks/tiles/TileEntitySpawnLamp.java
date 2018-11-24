package de.ellpeck.naturesaura.blocks.tiles;

public class TileEntitySpawnLamp extends TileEntityImpl {

    public int getRadius() {
        return this.redstonePower * 3;
    }

}
