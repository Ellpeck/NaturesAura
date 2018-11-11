package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.aura.AuraType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public interface IDrainSpotEffect {

    void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, MutableInt spot);

    boolean appliesToType(AuraType type);
}
