package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.BlockLog;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.Queue;

public class TileEntityOakGenerator extends TileEntityImpl implements ITickable {

    public Queue<BlockPos> scheduledBigTrees = new ArrayDeque<>();

    @Override
    public void update() {
        if (!this.world.isRemote)
            while (!this.scheduledBigTrees.isEmpty()) {
                BlockPos pos = this.scheduledBigTrees.remove();
                if (this.world.getBlockState(pos).getBlock() instanceof BlockLog) {
                    int toAdd = 100000;
                    boolean canGen = this.canGenerateRightNow(25, toAdd);
                    if (canGen)
                        while (toAdd > 0) {
                            BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 25, this.pos);
                            toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                        }

                    PacketHandler.sendToAllAround(this.world, this.pos, 32, new PacketParticles(
                            this.pos.getX(), this.pos.getY(), this.pos.getZ(), 12,
                            pos.getX(), pos.getY(), pos.getZ(), canGen ? 1 : 0));
                }
            }
    }
}
