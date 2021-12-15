package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Queue;

public class BlockEntityOakGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public Queue<BlockPos> scheduledBigTrees = new ArrayDeque<>();

    public BlockEntityOakGenerator(BlockPos pos, BlockState state) {
        super(ModTileEntities.OAK_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide)
            while (!this.scheduledBigTrees.isEmpty()) {
                var pos = this.scheduledBigTrees.remove();
                if (this.level.getBlockState(pos).getBlock().getTags().contains(BlockTags.LOGS.getName())) {
                    var toAdd = 100000;
                    var canGen = this.canGenerateRightNow(toAdd);
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
