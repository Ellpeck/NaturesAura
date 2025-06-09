package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityPickupStopper extends BlockEntityImpl {

    public BlockEntityPickupStopper(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PICKUP_STOPPER, pos, state);
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

    @Override
    public void onLoad() {
        super.onLoad();
        if (!this.level.isClientSide) {
            var data = (LevelData) ILevelData.getLevelData(this.level);
            data.pickupStoppers.add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!this.level.isClientSide) {
            var data = (LevelData) ILevelData.getLevelData(this.level);
            data.pickupStoppers.remove(this);
        }
    }

}
