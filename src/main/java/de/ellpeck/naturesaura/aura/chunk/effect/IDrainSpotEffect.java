package de.ellpeck.naturesaura.aura.chunk.effect;

import de.ellpeck.naturesaura.aura.AuraType;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableInt;

public interface IDrainSpotEffect {

    void update(World world, Chunk chunk, AuraChunk auraChunk, BlockPos pos, MutableInt spot);

    boolean appliesToType(AuraType type);
}
