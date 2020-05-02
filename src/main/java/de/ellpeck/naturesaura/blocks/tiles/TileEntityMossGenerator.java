package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMossGenerator extends TileEntityImpl implements ITickableTileEntity {

    public TileEntityMossGenerator() {
        super(ModTileEntities.MOSS_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.world.getGameTime() % 20 != 0)
                return;
            WorldData data = (WorldData) IWorldData.getWorldData(this.world);

            List<BlockPos> possibleOffsets = new ArrayList<>();
            int range = 2;
            for (int x = -range; x <= range; x++)
                for (int y = -range; y <= range; y++)
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.pos.add(x, y, z);
                        boolean isRecent = data.recentlyConvertedMossStones.contains(offset);
                        BlockState state = this.world.getBlockState(offset);
                        if (NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.inverse().containsKey(state)) {
                            if (isRecent)
                                continue;
                            possibleOffsets.add(offset);
                        } else if (isRecent) {
                            data.recentlyConvertedMossStones.remove(offset);
                        }
                    }

            if (possibleOffsets.isEmpty())
                return;
            BlockPos offset = possibleOffsets.get(this.world.rand.nextInt(possibleOffsets.size()));
            BlockState state = this.world.getBlockState(offset);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.inverse().get(state);

            int toAdd = 4000;
            if (this.canGenerateRightNow(35, toAdd)) {
                while (toAdd > 0) {
                    BlockPos spot = IAuraChunk.getLowestSpot(this.world, this.pos, 35, this.pos);
                    toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                }

                PacketHandler.sendToAllAround(this.world, this.pos, 32,
                        new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), PacketParticles.Type.MOSS_GENERATOR));
            }

            this.world.playEvent(2001, offset, Block.getStateId(state));
            this.world.setBlockState(offset, result);
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
