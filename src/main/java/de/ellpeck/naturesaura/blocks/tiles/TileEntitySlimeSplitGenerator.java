package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockEntitySlimeSplitGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    private int generationTimer;
    private int amountToRelease;
    private int color;

    public BlockEntitySlimeSplitGenerator() {
        super(ModTileEntities.SLIME_SPLIT_GENERATOR);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide || this.level.getGameTime() % 10 != 0)
            return;
        if (this.generationTimer > 0) {
            int amount = this.amountToRelease * 10;
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

    public void startGenerating(SlimeEntity slime) {
        int size = slime.getSlimeSize();
        this.generationTimer = size * 30;
        this.amountToRelease = (size * this.getGenerationAmount(slime)) / this.generationTimer;
        this.color = this.getSlimeColor(slime);

        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) slime.getPosX(), (float) slime.getPosY(), (float) slime.getPosZ(), PacketParticles.Type.SLIME_SPLIT_GEN_START,
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

    private int getSlimeColor(SlimeEntity slime) {
        if (slime instanceof MagmaCubeEntity) {
            return 0x942516;
        } else {
            return 0x4da84f;
        }
    }

    private int getGenerationAmount(SlimeEntity slime) {
        if (slime instanceof MagmaCubeEntity) {
            return 45000;
        } else {
            return 25000;
        }
    }
}
