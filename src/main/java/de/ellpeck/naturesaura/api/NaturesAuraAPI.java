package de.ellpeck.naturesaura.api;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.aura.type.BasicAuraType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.internal.StubHooks;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * The main class of the Nature's Aura API. This is where you can find recipe
 * lists and the {@link IInternalHooks} instance, which can be used to hook into
 * internal mod functions not exposed to the API.
 */
public final class NaturesAuraAPI {
    private static IInternalHooks instance = new StubHooks();

    public static final String MOD_ID = "naturesaura";
    public static final String API_ID = MOD_ID + "api";
    public static final String VERSION = "1";

    /**
     * The list of all {@link AltarRecipe} instances which are the recipes used
     * by the Natural Altar. Newly created recipes can be easily added using
     * {@link AltarRecipe#register()}.
     */
    public static final Map<ResourceLocation, AltarRecipe> ALTAR_RECIPES = new HashMap<>();
    /**
     * The list of all {@link TreeRitualRecipe} instances which are the recipes
     * used in the Ritual of the Forest. Newly created recipes can be easily
     * added using {@link TreeRitualRecipe#register()}.
     */
    public static final Map<ResourceLocation, TreeRitualRecipe> TREE_RITUAL_RECIPES = new HashMap<>();
    /**
     * The list of all types of flowers that the flower generator can use for
     * consumption. By default, all {@link BlockFlower} instances and all blocks
     * specified in the config file are added
     */
    public static final List<IBlockState> FLOWER_GENERATOR_BLOCKS = new ArrayList<>();
    /**
     * A map of all of the block states that the Botanist's Pickaxe can convert
     * into their mossy variations. Contains mossy brick and mossy cobblestone
     * by default, along with all blocks specified in the config file
     */
    public static final Map<IBlockState, IBlockState> BOTANIST_PICKAXE_CONVERSIONS = new HashMap<>();
    /**
     * A map of all {@link IAuraType} instances which are types of Aura present
     * in different types of worlds. {@link BasicAuraType} instances can be
     * easily registered using {@link BasicAuraType#register()}.
     */
    public static final Map<ResourceLocation, IAuraType> AURA_TYPES = new HashMap<>();
    public static final IAuraType TYPE_OVERWORLD = new BasicAuraType(new ResourceLocation(MOD_ID, "overworld"), DimensionType.OVERWORLD, 0xbef224).register();
    public static final IAuraType TYPE_NETHER = new BasicAuraType(new ResourceLocation(MOD_ID, "nether"), DimensionType.NETHER, 0x871c0c).register();
    public static final IAuraType TYPE_END = new BasicAuraType(new ResourceLocation(MOD_ID, "end"), DimensionType.THE_END, 0x302624).register();
    public static final IAuraType TYPE_OTHER = new BasicAuraType(new ResourceLocation(MOD_ID, "other"), null, 0x2fa8a0).register();
    /**
     * A map of all {@link IDrainSpotEffect} suppliers which are effects that
     * happen passively at every spot that Aura has been drained from in the
     * world. These effects include things like vegetational increase and
     * natural decay. To register your own drain spot effects, just add a
     * supplier for them to this map and they will automatically be executed
     * once a second for every drain spot currently loaded.
     */
    public static final Map<ResourceLocation, Supplier<IDrainSpotEffect>> DRAIN_SPOT_EFFECTS = new HashMap<>();

    /**
     * The capability for any item or block that stores Aura in the form of an
     * {@link IAuraContainer}
     */
    @CapabilityInject(IAuraContainer.class)
    public static Capability<IAuraContainer> capAuraContainer;
    /**
     * The capability for any item that can be recharged from an Aura storage
     * container like the Aura Cache in the form of {@link IAuraRecharge} by a
     * player holding it in their hand
     */
    @CapabilityInject(IAuraRecharge.class)
    public static Capability<IAuraRecharge> capAuraRecharge;
    /**
     * The capability that any chunk in a world has to store Aura in it. As this
     * is only applicable to chunks and all chunks in the world automatically
     * get assigned this capability, using it directly is not necessary for
     * addon developers. To retrieve this capability from any chunk, use the
     * helper method {@link IAuraChunk#getAuraChunk(World, BlockPos)}.
     */
    @CapabilityInject(IAuraChunk.class)
    public static Capability<IAuraChunk> capAuraChunk;

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
        void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade);

        /**
         * This method can be used to spawn the magic particle effect used by
         * Nature's Aura. The particle will be created to spawn at the start
         * position and move towards the end position, dying when it reaches it.
         * It will not have an effect on the client side, so if you want to send
         * it from the server side, you need to create your own packet.
         *
         * @param startX The start x
         * @param startY The start y
         * @param startZ The start z
         * @param endX   The end x
         * @param endY   The end y
         * @param endZ   The end z
         * @param speed  The speed at which the particle should go
         * @param color  The color of the particle
         * @param scale  The scale of the particle
         */
        void spawnParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale);

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
