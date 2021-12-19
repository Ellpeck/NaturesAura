package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityAnimalGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private int timeRemaining;
    private int amountToRelease;

    public BlockEntityAnimalGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANIMAL_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 10 != 0)
                return;
            if (this.timeRemaining <= 0)
                return;

            var remain = this.amountToRelease;
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
