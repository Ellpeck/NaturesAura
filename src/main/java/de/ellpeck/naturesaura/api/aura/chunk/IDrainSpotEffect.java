package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IDrainSpotEffect {

    void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot);

    boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type);

    ResourceLocation getName();
}
