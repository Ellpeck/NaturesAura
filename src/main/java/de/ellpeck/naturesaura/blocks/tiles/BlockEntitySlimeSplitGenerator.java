package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntitySlimeSplitGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private int generationTimer;
    private int amountToRelease;
    private int color;

    public BlockEntitySlimeSplitGenerator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLIME_SPLIT_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide || this.level.getGameTime() % 10 != 0)
            return;
        if (this.generationTimer > 0) {
            var amount = this.amountToRelease * 10;
            if (this.canGenerateRightNow(amount)) {
                this.generateAura(amount);
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.SLIME_SPLIT_GEN_CREATE, this.color));
            }
            this.generationTimer -= 10;
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }

    public boolean isBusy() {
        return this.generationTimer > 0;
    }

    public void startGenerating(Slime slime) {
        var size = slime.getSize();
        this.generationTimer = size * 30;
        this.amountToRelease = (size * this.getGenerationAmount(slime)) / this.generationTimer;
        this.color = this.getSlimeColor(slime);

        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) slime.getX(), (float) slime.getY(), (float) slime.getZ(), PacketParticles.Type.SLIME_SPLIT_GEN_START,
                this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.color));
    }

    @Override
    public void writeNBT(CompoundTag compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type == SaveType.TILE) {
            compound.putInt("timer", this.generationTimer);
            compound.putInt("amount", this.amountToRelease);
            compound.putInt("color", this.color);
        }
    }

    @Override
    public void readNBT(CompoundTag compound, SaveType type) {
        super.readNBT(compound, type);
        if (type == SaveType.TILE) {
            this.generationTimer = compound.getInt("timer");
            this.amountToRelease = compound.getInt("amount");
            this.color = compound.getInt("color");
        }
    }

    private int getSlimeColor(Slime slime) {
        if (slime instanceof MagmaCube) {
            return 0x942516;
        } else {
            return 0x4da84f;
        }
    }

    private int getGenerationAmount(Slime slime) {
        if (slime instanceof MagmaCube) {
            return 45000;
        } else {
            return 25000;
        }
    }
}
