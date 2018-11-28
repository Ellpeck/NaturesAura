package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityAnimalGenerator extends TileEntityImpl implements ITickable {

    private int timeRemaining;
    private int amountToRelease;

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 10 != 0)
                return;
            if (this.timeRemaining <= 0)
                return;

            int remain = this.amountToRelease;
            while (remain > 0) {
                BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 35, this.pos);
                remain -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, remain);
            }

            this.timeRemaining -= 10;

            PacketHandler.sendToAllAround(this.world, this.pos, 32,
                    new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 16));
        }
    }

    public boolean isBusy() {
        return this.timeRemaining > 0;
    }

    public void setGenerationValues(int time, int amount) {
        this.timeRemaining = time;
        this.amountToRelease = amount;
    }
}
