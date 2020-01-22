package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;

public class TileEntityAuraDetector extends TileEntityImpl implements ITickableTileEntity {

    public int redstonePower;

    public TileEntityAuraDetector() {
        super(ModTileEntities.AURA_DETECTOR);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.world.getGameTime() % 20 == 0) {
            int totalAmount = IAuraChunk.triangulateAuraInArea(this.world, this.pos, 25);
            int power = MathHelper.clamp(MathHelper.ceil(totalAmount / (IAuraChunk.DEFAULT_AURA * 2F) * 15F), 0, 15);
            if (this.redstonePower != power) {
                this.redstonePower = power;
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockState().getBlock());
            }
        }
    }
}
