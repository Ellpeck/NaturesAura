package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityMossGenerator extends BlockEntityImpl implements ITickableBlockEntity {

    public BlockEntityMossGenerator(BlockPos pos, BlockState state) {
        super(ModTileEntities.MOSS_GENERATOR, pos, state);
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
                        BlockPos offset = this.worldPosition.offset(x, y, z);
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
            BlockPos offset = possibleOffsets.get(this.level.random.nextInt(possibleOffsets.size()));
            BlockState state = this.level.getBlockState(offset);
            BlockState result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.inverse().get(state);

            int toAdd = 7000;
            if (this.canGenerateRightNow(toAdd)) {
                this.generateAura(toAdd);
                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                        new PacketParticles(offset.getX(), offset.getY(), offset.getZ(), PacketParticles.Type.MOSS_GENERATOR));
            }

            this.level.levelEvent(2001, offset, Block.getId(state));
            this.level.setBlockAndUpdate(offset, result);
        }
    }

    @Override
    public boolean wantsLimitRemover() {
        return true;
    }
}
