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
        super(ModBlockEntities.MOSS_GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.level.getGameTime() % 20 != 0)
                return;
            var data = (LevelData) ILevelData.getLevelData(this.level);

            List<BlockPos> possibleOffsets = new ArrayList<>();
            var range = 2;
            for (var x = -range; x <= range; x++)
                for (var y = -range; y <= range; y++)
                    for (var z = -range; z <= range; z++) {
                        var offset = this.worldPosition.offset(x, y, z);
                        var isRecent = data.recentlyConvertedMossStones.contains(offset);
                        var state = this.level.getBlockState(offset);
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
            var offset = possibleOffsets.get(this.level.random.nextInt(possibleOffsets.size()));
            var state = this.level.getBlockState(offset);
            var result = NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.inverse().get(state);

            var toAdd = 7000;
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
