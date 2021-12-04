package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockEntityAnimalGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private int timeRemaining;
    private int amountToRelease;

    public BlockEntityAnimalGenerator() {
        super(ModTileEntities.ANIMAL_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;
            if (this.timeRemaining <= 0)
                return;

            int remain = this.amountToRelease;
            if (this.canGenerateRightNow(remain)) {
                this.generateAura(remain);
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.ANIMAL_GEN_CREATE));
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
