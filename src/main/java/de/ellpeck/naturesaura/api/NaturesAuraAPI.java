package de.ellpeck.naturesaura.api;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.internal.StubHooks;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The main class of the Nature's Aura API. This is where you can find recipe
 * lists and the {@link IInternalHooks} instance, which can be used to hook into
 * internal mod functions not exposed to the API.
 */
public final class NaturesAuraAPI {
    private static IInternalHooks instance = new StubHooks();

    /**
     * The list of all {@link AltarRecipe} instances which are the recipes used
     * by the Natural Altar. Newly created recipes are automatically added to
     * this list.
     */
    public static final Map<ResourceLocation, AltarRecipe> ALTAR_RECIPES = new HashMap<>();
    /**
     * The list of all {@link TreeRitualRecipe} instances which are the recipes
     * used in the Ritual of the Forest. Newly created recipes are automatically
     * added to this list.
     */
    public static final Map<ResourceLocation, TreeRitualRecipe> TREE_RITUAL_RECIPES = new HashMap<>();

    /**
     * This method returns the active {@link IInternalHooks} instance which can
     * be used to hook into the mod's internal functionalities. This is
     * instantiated as {@link StubHooks} by default which has no functionality,
     * but, in the mod's preInit phase, this will be overriden to a proper
     * implementation. If you want to use this instance, use it after Nature's
     * Aura's preInit phase.
     *
     * @return The active {@link IInternalHooks} instance
     */
    public static IInternalHooks instance() {
        return instance;
    }

    /**
     * This is an internal function. Do not use.
     */
    public static void setInstance(IInternalHooks inst) {
        if (instance instanceof StubHooks)
            instance = inst;
        else
            throw new IllegalStateException();
    }

    /**
     * @see #instance()
     */
    public interface IInternalHooks {

        /**
         * Helper method to extract aura from an {@link IAuraContainer} in the
         * supplied player's inventory or baubles slots. The method returns true
         * if the aura could be extracted. Note that, if the player is in
         * creative mode, this method will always return true and no extraction
         * will take place.
         *
         * @param player   The player
         * @param amount   The amount to extract
         * @param simulate If the extraction should be simulated
         * @return If the extraction was successful
         */
        boolean extractAuraFromPlayer(EntityPlayer player, int amount, boolean simulate);

        /**
         * This method can be used to spawn the magic particle effect used by
         * Nature's Aura. It will not have an effect on the client side, so if
         * you want to send it from the server side, you need to create your own
         * packet.
         *
         * @param world     The world to spawn the particle in
         * @param posX      The x position
         * @param posY      The y position
         * @param posZ      The z position
         * @param motionX   The x motion
         * @param motionY   The y motion
         * @param motionZ   The z motion
         * @param color     The color the particle should have, in hex
         * @param scale     The scale of the particle
         * @param maxAge    The max age before the particle should die
         * @param gravity   The amount of gravity the particle should have, can
         *                  be 0
         * @param collision If the particle should collide with blocks
         * @param fade      If the particle should slowly fade out or suddenly
         *                  disappear
         */
        void spawnMagicParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade);

        /**
         * @see IAuraChunk#getSpotsInArea(World, BlockPos, int, BiConsumer)
         */
        void getAuraSpotsInArea(World world, BlockPos pos, int radius, BiConsumer<BlockPos, MutableInt> consumer);

        /**
         * @see IAuraChunk#getAuraInArea(World, BlockPos, int)
         */
        int getAuraInArea(World world, BlockPos pos, int radius);

        /**
         * @see IAuraChunk#getLowestSpot(World, BlockPos, int, BlockPos)
         */
        BlockPos getLowestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot);

        /**
         * @see IAuraChunk#getHighestSpot(World, BlockPos, int, BlockPos)
         */
        BlockPos getHighestAuraDrainSpot(World world, BlockPos pos, int radius, BlockPos defaultSpot);
    }

}
