package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;

/**
 * A class whose instances hold information about the aura present in any given
 * {@link Chunk}. To get an instance for a chunk, use {@link
 * #getAuraChunk(IWorld, BlockPos)}.
 * <p>
 * It is not intended for API users to create custom implementation of this
 * class.
 */
public interface IAuraChunk extends INBTSerializable<CompoundNBT> {

    /**
     * The default amount of Aura that a chunk has stored
     */
    int DEFAULT_AURA = 1000000;

    /**
     * This method is used to get information about the Aura in any given chunk.
     * This is a convenience method.
     *
     * @param world The world
     * @param pos   A position that the chunk contains
     * @return The {@link IAuraChunk} instance belonging to the chunk
     */
    static IAuraChunk getAuraChunk(IWorld world, BlockPos pos) {
        Chunk chunk = (Chunk) world.getChunk(pos);
        return chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
    }

    /**
     * This method uses the supplied consumer to iterate over all the drain
     * spots, represented as a position and the number of Aura in them, in any
     * given area.
     *
     * @param world    The world
     * @param pos      The center position
     * @param radius   The radius around the center to search for spots in
     * @param consumer A consumer that gets given the position and amount of
     *                 aura in each drain spot found
     */
    static void getSpotsInArea(IWorld world, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        NaturesAuraAPI.instance().getAuraSpotsInArea((World) world, pos, radius, consumer);
    }

    /**
     * Convenience method that adds up the amount of aura spots from {@link
     * #getSpotsInArea(IWorld, BlockPos, int, BiConsumer)} and returns it.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of spots found in the area
     */
    static int getSpotAmountInArea(IWorld world, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getSpotAmountInArea((World) world, pos, radius);
    }

    /**
     * Convenience method that adds up all of the aura from each drain spot from
     * {@link #getSpotsInArea(IWorld, BlockPos, int, BiConsumer)} and
     * conveniently returns it. For a better visual display with a more gradual
     * increase, use {@link #triangulateAuraInArea(IWorld, BlockPos, int)}.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of Aura present in that area, based on the drain spots
     * that are found
     */
    static int getAuraInArea(IWorld world, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getAuraInArea((World) world, pos, radius);
    }

    /**
     * Convenience method that combines {@link #getAuraInArea(IWorld, BlockPos,
     * int)} and {@link #getSpotAmountInArea(IWorld, BlockPos, int)} to increase
     * performance.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return A pair of the amount of aura in the area as the {@link
     * Pair#getLeft()} entry, and the amount of aura spots in the area as the
     * {@link Pair#getRight()} entry
     */
    static Pair<Integer, Integer> getAuraAndSpotAmountInArea(World world, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getAuraAndSpotAmountInArea(world, pos, radius);
    }

    /**
     * Convenience method that adds up all of the aura from each drain spot from
     * {@link #getSpotsInArea(IWorld, BlockPos, int, BiConsumer)}, but
     * multiplies their amount by the percentual distance to the supplied
     * position. This will cause for a lot more gradual of an increase and
     * decrease of Aura when moving closer to actual spots. This should be used
     * for visual purposes as it is more performance intensive than {@link
     * #getAuraInArea(IWorld, BlockPos, int)}.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of Aura presetn in that area, based on the drain spots
     * that are found and their distance to the center
     */
    static int triangulateAuraInArea(IWorld world, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().triangulateAuraInArea((World) world, pos, radius);
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
    static BlockPos getLowestSpot(IWorld world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getLowestAuraDrainSpot((World) world, pos, radius, defaultSpot);
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
    static BlockPos getHighestSpot(IWorld world, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getHighestAuraDrainSpot((World) world, pos, radius, defaultSpot);
    }

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
