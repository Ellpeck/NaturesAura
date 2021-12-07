package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntitySpawnLamp extends BlockEntityImpl {

    public BlockEntitySpawnLamp(BlockPos pos, BlockState state) {
        super(ModTileEntities.SPAWN_LAMP, pos, state);
    }

    public int getRadius() {
        return this.redstonePower * 3;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!this.level.isClientSide) {
            LevelData data = (LevelData) ILevelData.getLevelData(this.level);
            data.spawnLamps.add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
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
