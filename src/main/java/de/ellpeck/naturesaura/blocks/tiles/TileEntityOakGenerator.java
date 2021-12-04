package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlockEntityOakGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public Queue<BlockPos> scheduledBigTrees = new ArrayDeque<>();

    public BlockEntityOakGenerator() {
        super(ModTileEntities.OAK_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide)
            while (!this.scheduledBigTrees.isEmpty()) {
                BlockPos pos = this.scheduledBigTrees.remove();
                if (this.level.getBlockState(pos).getBlock().getTags().contains(BlockTags.LOGS.getName())) {
                    int toAdd = 100000;
                    boolean canGen = this.canGenerateRightNow(toAdd);
                    if (canGen)
                        this.generateAura(toAdd);

                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                            this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.OAK_GENERATOR,
                            pos.getX(), pos.getY(), pos.getZ(), canGen ? 1 : 0));
                }
            }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
