package de.ellpeck.naturesaura.api.aura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;

/**
 * A class whose instances hold information about the aura present in any given
 * {@link net.minecraft.world.level.chunk.LevelChunk}. To get an instance for a
 * chunk, use {@link #getAuraChunk(Level, BlockPos)}.
 * <p>
 * It is not intended for API users to create custom implementation of this
 * class.
 */
public interface IAuraChunk extends INBTSerializable<CompoundTag> {

    /**
     * The default amount of Aura that a chunk has stored
     */
    int DEFAULT_AURA = 1000000;

    /**
     * This method is used to get information about the Aura in any given chunk.
     * This is a convenience method.
     *
     * @param level The level
     * @param pos   A position that the chunk contains
     * @return The {@link IAuraChunk} instance belonging to the chunk
     */
    static IAuraChunk getAuraChunk(Level level, BlockPos pos) {
        LevelChunk chunk = (LevelChunk) level.getChunk(pos);
        return chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
    }

    /**
     * This method uses the supplied consumer to iterate over all the drain
     * spots, represented as a position and the number of Aura in them, in any
     * given area.
     *
     * @param level    The level
     * @param pos      The center position
     * @param radius   The radius around the center to search for spots in
     * @param consumer A consumer that gets given the position and amount of
     *                 aura in each drain spot found
     */
    static void getSpotsInArea(Level level, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer) {
        NaturesAuraAPI.instance().getAuraSpotsInArea(level, pos, radius, consumer);
    }

    /**
     * Convenience method that adds up the amount of aura spots from {@link
     * #getSpotsInArea(Level, BlockPos, int, BiConsumer)} and returns it.
     *
     * @param level  The level
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of spots found in the area
     */
    static int getSpotAmountInArea(Level level, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getSpotAmountInArea(level, pos, radius);
    }

    /**
     * Convenience method that adds up all of the aura from each drain spot from
     * {@link #getSpotsInArea(Level, BlockPos, int, BiConsumer)} and
     * conveniently returns it. For a better visual display with a more gradual
     * increase, use {@link #triangulateAuraInArea(Level, BlockPos, int)}.
     *
     * @param level  The level
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of Aura present in that area, based on the drain spots
     * that are found
     */
    static int getAuraInArea(Level level, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getAuraInArea(level, pos, radius);
    }

    /**
     * Convenience method that combines {@link #getAuraInArea(Level, BlockPos,
     * int)} and {@link #getSpotAmountInArea(Level, BlockPos, int)} to increase
     * performance.
     *
     * @param level  The level
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return A pair of the amount of aura in the area as the {@link
     * Pair#getLeft()} entry, and the amount of aura spots in the area as the
     * {@link Pair#getRight()} entry
     */
    static Pair<Integer, Integer> getAuraAndSpotAmountInArea(Level level, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().getAuraAndSpotAmountInArea(level, pos, radius);
    }

    /**
     * Convenience method that adds up all of the aura from each drain spot from
     * {@link #getSpotsInArea(Level, BlockPos, int, BiConsumer)}, but multiplies
     * their amount by the percentual distance to the supplied position. This
     * will cause for a lot more gradual of an increase and decrease of Aura
     * when moving closer to actual spots. This should be used for visual
     * purposes as it is more performance intensive than {@link
     * #getAuraInArea(Level, BlockPos, int)}.
     *
     * @param level  The level
     * @param pos    The center position
     * @param radius The radius around the center to search for spots in
     * @return The amount of Aura presetn in that area, based on the drain spots
     * that are found and their distance to the center
     */
    static int triangulateAuraInArea(Level level, BlockPos pos, int radius) {
        return NaturesAuraAPI.instance().triangulateAuraInArea(level, pos, radius);
    }

    /**
     * This method returns the position of the lowest drain spot (meaning the
     * one that has the least Aura stored) in the given area. This should be
     * used with any machines that fill up Aura in an area, so that the most
     * drained spots get selected first. Note that, when there is no drain spot
     * with an amount lower than 0, the default will always be returned.
     *
     * @param level       The level
     * @param pos         The center position
     * @param radius      The radius around the center to search for spots in
     * @param defaultSpot A position that will be used to create a new drain
     *                    spot when none are found
     * @return The position of the lowest drain spot
     */
    static BlockPos getLowestSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getLowestAuraDrainSpot(level, pos, radius, defaultSpot);
    }

    /**
     * This method returns the position of the highest drain spot (meaning the
     * one that has the most Aura stored) in the given area. This should be used
     * with any machines that use up Aura so that the spots with the highest
     * amount are drained first. Note that, when there is no drain spot with an
     * amount greater than 0, the defautl will always be returned.
     *
     * @param level       The level
     * @param pos         The center position
     * @param radius      The radius around the center to search for spots in
     * @param defaultSpot A position that will be used to create a new drain
     *                    spot when none are found
     * @return The position of the highest drain spot
     */
    static BlockPos getHighestSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot) {
        return NaturesAuraAPI.instance().getHighestAuraDrainSpot(level, pos, radius, defaultSpot);
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
