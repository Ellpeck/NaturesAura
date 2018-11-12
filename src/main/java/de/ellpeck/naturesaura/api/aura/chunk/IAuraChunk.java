package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.NACapabilities;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.function.BiConsumer;

/**
 * A class whose instances hold information about the aura present in any given
 * {@link Chunk}. To get an instance for a chunk, use {@link
 * #getAuraChunk(World, BlockPos)}.
 * <p>
 * It is not intended for API users to create custom implementation of this
 * class.
 */
public interface IAuraChunk extends ICapabilityProvider, INBTSerializable<NBTTagCompound> {
    /**
     * The default amount of Aura that a chunk has stored
     */
    int DEFAULT_AURA = 10000;

    /**
     * This method is used to get information about the Aura in any given chunk.
     * This is a convenience method.
     *
     * @param world The world
     * @param pos   A position that the chunk contains
     * @return The {@link IAuraChunk} instance belonging to the chunk
     */
    static IAuraChunk getAuraChunk(World world, BlockPos pos) {
        Chunk chunk = world.getChunk(pos);
        if (chunk.hasCapability(NACapabilities.auraChunk, null)) {
            return chunk.getCapability(NACapabilities.auraChunk, null);
        } else {
            return null;
        }
    }

    /**
     * This method uses the supplied consumer to iterate over all the drain
     * spots, represented as a position and the number of Aura in them, in any
     * given area.
     * <p>
     * Notice that this is different from {@link #getSpotsInArea(BlockPos, int,
     * BiConsumer)} because this method iterates over several chunks while the
     * former only uses the current aura chunk instance. Most of the time, you
     * will want to use this method.
     *
     * @param world    The world
     * @param pos      The center position
     * @param radius   The radius around the center to search for spots in
     * @param consumer A consumer that gets given the position and amount of
     *                 aura in each drain spot found
     */
    static void getSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer) {
        NaturesAuraAPI.instance().getAuraSpotsInArea(world, pos, radius, consumer);
    }

    /**
     * Convenience method that adds up all of the aura from each drain spot from
     * {@link #getSpotsInArea(World, BlockPos, int, BiConsumer)} and
     * conveniently returns it.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of Aura present in that area, based on the drain spots
     * that are found
     */
    static int getAuraInArea(World world, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getAuraInArea(world, pos, radius);
    }

    /**
     * This method returns the position of the lowest drain spot (meaning the
     * one that has the least Aura stored) in the given area. This should be
     * used with any machines that fill up Aura in an area, so that the most
     * drained spots get selected first.
     *
     * @param world       The world
     * @param pos         The center position
     * @param radius      The radius around the center to search for spots in
     * @param defaultSpot A position that will be used to create a new drain
     *                    spot when none are found
     * @return The position of the lowest drain spot
     */
    static BlockPos getLowestSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getLowestAuraDrainSpot(world, pos, radius, defaultSpot);
    }

    /**
     * This method returns the position of the highest drain spot (meaning the
     * one that has the most Aura stored) in the given area. This should be used
     * with any machines that use up Aura so that the spots with the highest
     * amount are drained first.
     *
     * @param world       The world
     * @param pos         The center position
     * @param radius      The radius around the center to search for spots in
     * @param defaultSpot A position that will be used to create a new drain
     *                    spot when none are found
     * @return The position of the highest drain spot
     */
    static BlockPos getHighestSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getHighestAuraDrainSpot(world, pos, radius, defaultSpot);
    }

    void addEffect(IDrainSpotEffect effect);

    /**
     * @see #getSpotsInArea(World, BlockPos, int, BiConsumer)
     */
    void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer);

    void drainAura(BlockPos pos, int amount);

    void storeAura(BlockPos pos, int amount);

    MutableInt getDrainSpot(BlockPos pos);

    IAuraType getType();

    void markDirty();
}
