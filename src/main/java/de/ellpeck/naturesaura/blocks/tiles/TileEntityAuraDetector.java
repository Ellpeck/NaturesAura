package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class TileEntityAuraDetector extends TileEntityImpl implements ITickable {

    public int redstonePower;

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 80 == 0) {
            MutableFloat totalAmount = new MutableFloat(IAuraChunk.DEFAULT_AURA);
            IAuraChunk.getSpotsInArea(this.world, this.pos, 25, (pos, spot) -> {
                float percentage = 1F - (float) this.pos.getDistance(pos.getX(), pos.getY(), pos.getZ()) / 25F;
                totalAmount.add(spot * percentage);
            });
            int power = MathHelper.clamp(MathHelper.ceil(totalAmount.intValue() / (IAuraChunk.DEFAULT_AURA * 2F) * 15F), 0, 15);
            if (this.redstonePower != power) {
                this.redstonePower = power;
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
    }
}
