package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.Mth;

public class BlockEntityAuraDetector extends BlockEntityImpl implements ITickableBlockEntity {

    public int redstonePower;

    public BlockEntityAuraDetector() {
        super(ModTileEntities.AURA_DETECTOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.level.getGameTime() % 20 == 0) {
            int totalAmount = IAuraChunk.triangulateAuraInArea(this.level, this.worldPosition, 25);
            int power = Mth.clamp(Mth.ceil(totalAmount / (IAuraChunk.DEFAULT_AURA * 2F) * 15F), 0, 15);
            if (this.redstonePower != power) {
                this.redstonePower = power;
                this.level.updateComparatorOutputLevel(this.worldPosition, this.getBlockState().getBlock());
            }
        }
    }
}
