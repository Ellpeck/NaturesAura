package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityAnimalGenerator extends TileEntityImpl implements ITickableTileEntity {

    private int timeRemaining;
    private int amountToRelease;

    public TileEntityAnimalGenerator() {
        super(ModTileEntities.ANIMAL_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 10 != 0)
                return;
            if (this.timeRemaining <= 0)
                return;

            int remain = this.amountToRelease;
            if (this.canGenerateRightNow(remain)) {
                this.generateAura(remain);
                PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.ANIMAL_GEN_CREATE));
            }

            this.timeRemaining -= 10;
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }

    public boolean isBusy() {
        return this.timeRemaining > 0;
    }

    public void setGenerationValues(int time, int amount) {
        this.timeRemaining = time;
        this.amountToRelease = amount;
    }
}
