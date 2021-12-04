package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityMossGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityMossGenerator() {
        super(ModTileEntities.MOSS_GENERATOR);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 20 != 0)
                return;
            LevelData data = (LevelData) ILevelData.getLevelData(this.level);

            List<BlockPos> possibleOffsets = new ArrayList<>();
            int range = 2;
            for (int x = -range; x <= range; x++)
                for (int y = -range; y <= range; y++)
                    for (int z = -range; z <= range; z++) {
                        BlockPos offset = this.worldPosition.add(x, y, z);
                        boolean isRecent = data.recentlyConvertedMossStones.contains(offset);
                        BlockState state = this.level.getBlockState(offset);
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
            BlockPos offset = possibleOffsets.get(this.level.rand.nextInt(possibleOffsets.size()));
            BlockState state = this.level.getBlockState(offset);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.inverse().get(state);

            int toAdd = 7000;
            if (this.canGenerateRightNow(toAdd)) {
                this.generateAura(toAdd);
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), PacketParticles.Type.MOSS_GENERATOR));
            }

            this.level.playEvent(2001, offset, Block.getStateId(state));
            this.level.setBlockState(offset, result);
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
