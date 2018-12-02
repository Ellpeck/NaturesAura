package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

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
        if (chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
            return chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
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
    static void getSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
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
     * drained spots get selected first. Note that, when there is no drain spot
     * with an amount lower than 0, the default will always be returned.
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
     * amount are drained first. Note that, when there is no drain spot with an
     * amount greater than 0, the defautl will always be returned.
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

    /**
     * @see #getSpotsInArea(World, BlockPos, int, BiConsumer)
     */
    void getSpotsInArea(BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer);

    /**
     * Drains the given amount of Aura from the given position. Returns the
     * amount of Aura that was drained.
     *
     * @param pos        The position
     * @param amount     The amount to drain
     * @param aimForZero If true, and draining the given amount would make the
     *                   level go from positive to negative, an amount will be
     *                   drained instead that will cause the spot's amount to be
     *                   0.
     * @return The amount of Aura drained. Will only be different from the
     * supplied amount if stopAtZero is true
     */
    int drainAura(BlockPos pos, int amount, boolean aimForZero, boolean simulate);

    /**
     * Convenience version of {@link #drainAura(BlockPos, int, boolean,
     * boolean)} with aimForZero and simulate set to false, as this is the most
     * likely behavior you will want. Notice that {@link #storeAura(BlockPos,
     * int)} has aimForZero set to true.
     */
    int drainAura(BlockPos pos, int amount);

    /**
     * Stores the given amount of Aura at the given position. Returns the amount
     * of Aura that was stored.
     *
     * @param pos        The position
     * @param amount     The amount to store
     * @param aimForZero If true, and storing the given amount would make the
     *                   level go from negative to positive, an amount will be
     *                   stored instead that will cause the spot's amount to be
     *                   0.
     * @return The amount of Aura stored. Will only be different from the
     * supplied amount if stopAtZero is true
     */
    int storeAura(BlockPos pos, int amount, boolean aimForZero, boolean simulate);

    /**
     * Convenience version of {@link #storeAura(BlockPos, int, boolean,
     * boolean)} with aimForZero set to true and simulate set to false, as this
     * is the most likely behavior you will want. Notice that {@link
     * #drainAura(BlockPos, int)} has aimForZero set to false.
     */
    int storeAura(BlockPos pos, int amount);

    int getDrainSpot(BlockPos pos);

    IAuraType getType();

    void markDirty();
}
