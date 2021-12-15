package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityAuraDetector extends BlockEntityImpl implements ITickableBlockEntity {

    public int redstonePower;

    public BlockEntityAuraDetector(BlockPos pos, BlockState state) {
        super(ModTileEntities.AURA_DETECTOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 20 == 0) {
            var totalAmount = IAuraChunk.triangulateAuraInArea(this.level, this.worldPosition, 25);
            var power = Mth.clamp(Mth.ceil(totalAmount / (IAuraChunk.DEFAULT_AURA * 2F) * 15F), 0, 15);
            if (this.redstonePower != power) {
                this.redstonePower = power;
                this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
            }
        }
    }
}
