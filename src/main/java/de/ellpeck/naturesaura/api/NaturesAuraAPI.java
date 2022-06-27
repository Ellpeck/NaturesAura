package de.ellpeck.naturesaura.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.aura.type.BasicAuraType;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.api.misc.WeatherType;
import de.ellpeck.naturesaura.api.misc.WeightedOre;
import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.commons.lang3.tuple.Pair;

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

    public static final String MOD_ID = "naturesaura";

    /**
     * A map of all the block states that the Botanist's Pickaxe can convert
     * into their mossy variations. Contains mossy brick and mossy cobblestone
     * by default, along with all blocks specified in the config file
     */
    public static final BiMap<BlockState, BlockState> BOTANIST_PICKAXE_CONVERSIONS = HashBiMap.create();
    /**
     * A map of all {@link IAuraType} instances which are types of Aura present
     * in different types of levels. {@link BasicAuraType} instances can be
     * easily registered using {@link BasicAuraType#register()}.
     */
    public static final Map<ResourceLocation, IAuraType> AURA_TYPES = new HashMap<>();
    public static final BasicAuraType TYPE_OVERWORLD = new BasicAuraType(new ResourceLocation(NaturesAuraAPI.MOD_ID, "overworld"), Level.OVERWORLD, 0x89cc37, 0).register();
    public static final BasicAuraType TYPE_NETHER = new BasicAuraType(new ResourceLocation(NaturesAuraAPI.MOD_ID, "nether"), Level.NETHER, 0x871c0c, 0).register();
    public static final BasicAuraType TYPE_END = new BasicAuraType(new ResourceLocation(NaturesAuraAPI.MOD_ID, "end"), Level.END, 0x302624, 0).register();
    public static final BasicAuraType TYPE_OTHER = new BasicAuraType(new ResourceLocation(NaturesAuraAPI.MOD_ID, "other"), null, 0x2fa8a0, Integer.MIN_VALUE).register();
    /**
     * A map of all {@link IDrainSpotEffect} suppliers which are effects that
     * happen passively at every spot that Aura has been drained from in the
     * level. These effects include things like vegetational increase and
     * natural decay. To register your own drain spot effects, just add a
     * supplier for them to this map, and they will automatically be executed
     * once a second for every drain spot currently loaded.
     */
    public static final Map<ResourceLocation, Supplier<IDrainSpotEffect>> DRAIN_SPOT_EFFECTS = new HashMap<>();
    /**
     * A map of all effect powder type. The integer the effect is registered to
     * is the color that the powder and its effect should have. To check if a
     * powder is active in any given area, use {@link IInternalHooks#isEffectPowderActive(Level,
     * BlockPos, ResourceLocation)}
     */
    public static final Map<ResourceLocation, Integer> EFFECT_POWDERS = new HashMap<>();
    /**
     * A map of all {@link IMultiblock} objects which are multiblock structures
     * that can easily be looped through and checked, and also easily created
     * using the multiblock maker debug tool.
     */
    public static final Map<ResourceLocation, IMultiblock> MULTIBLOCKS = new HashMap<>();
    /**
     * A list of all {@link WeightedOre} objects that represent ores that can
     * spawn inside of stone blocks in the overworld
     */
    public static final List<WeightedOre> OVERWORLD_ORES = new ArrayList<>();
    /**
     * A list of all {@link WeightedOre} objects that represent ores that can
     * spawn inside netherrack blocks in the nether
     */
    public static final List<WeightedOre> NETHER_ORES = new ArrayList<>();
    /**
     * A map of all the entities' registry names to the amounts of aura they
     * each generate in the projectile generator
     */
    public static final Map<EntityType<?>, Integer> PROJECTILE_GENERATIONS = new HashMap<>();
    /**
     * A map of all the items that cause the {@link WeatherType} to be changed
     * using the weather changer
     */
    public static final Map<ItemStack, WeatherType> WEATHER_CHANGER_CONVERSIONS = new HashMap<>();
    /**
     * The capability for any item or block that stores Aura in the form of an
     * {@link IAuraContainer}
     */
    public static final Capability<IAuraContainer> CAP_AURA_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {
    });
    /**
     * The capability for any item that can be recharged from an Aura storage
     * container like the Aura Cache in the form of {@link IAuraRecharge} by a
     * player holding it in their hand
     */
    public static final Capability<IAuraRecharge> CAP_AURA_RECHARGE = CapabilityManager.get(new CapabilityToken<>() {
    });
    /**
     * The capability that any chunk in a level has to store Aura in it. As this
     * is only applicable to chunks and all chunks in the level automatically
     * get assigned this capability, using it directly is not necessary for
     * addon developers. To retrieve this capability from any chunk, use the
     * helper method {@link IAuraChunk#getAuraChunk(net.minecraft.world.level.Level,
     * BlockPos)}.
     */
    public static final Capability<IAuraChunk> CAP_AURA_CHUNK = CapabilityManager.get(new CapabilityToken<>() {
    });
    /**
     * The capability that any level has to store Nature's Aura specific data in
     * it. To retrieve this capability from any level, use the helper methods
     * {@link ILevelData#getLevelData(net.minecraft.world.level.Level)} or
     * {@link ILevelData#getOverworldData(net.minecraft.world.level.Level)}.
     */
    public static final Capability<ILevelData> CAP_LEVEL_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static final IInternalHooks INSTANCE;

    static {
        try {
            INSTANCE = (IInternalHooks) Class.forName("de.ellpeck.naturesaura.InternalHooks").getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method returns the active {@link IInternalHooks} instance which can
     * be used to hook into the mod's internal functionalities.
     *
     * @return The active {@link IInternalHooks} instance
     */
    public static IInternalHooks instance() {
        return NaturesAuraAPI.INSTANCE;
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
        boolean extractAuraFromPlayer(Player player, int amount, boolean simulate);

        /**
         * Helper method to insert aura into an {@link IAuraContainer} in the
         * supplied player's inventory or baubles slots. The method returns true
         * if the aura could be inserted.
         *
         * @param player   The player
         * @param amount   The amount to insert
         * @param simulate If the insertion should be simulated
         * @return If the insertion was successful
         */
        boolean insertAuraIntoPlayer(Player player, int amount, boolean simulate);

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
         * Sets wether Nature's Aura particles that are spawned will be rendered
         * with depth test enabled or not. Default value is true, please reset
         * after changing.
         *
         * @param depth Wether depth test should be enabled or not
         */
        void setParticleDepth(boolean depth);

        /**
         * Sets the range that Nature's Aura particles that are spawned will
         * have to have from the player at most to actually be spawned. Default
         * value is 32, please reset after changing.
         *
         * @param range The range that particle spawning should have
         */
        void setParticleSpawnRange(int range);

        void setParticleCulling(boolean cull);

        /**
         * This method is used to create a custom multiblock from within the
         * API. The multiblock will automatically be registered both to Nature's
         * Aura's multiblock registry and Patchouli's multiblock registry.
         *
         * @param name        The name the multiblock should have
         * @param pattern     The pattern that the multiblock should have, where
         *                    each character is mapped to a raw matcher
         * @param rawMatchers Each char matcher in the form of the char followed
         *                    by a matcher, either in the form of a Block, an
         *                    BlockState or a {@link Matcher}, similar to the
         *                    old way that crafting recipes work.
         * @return the multiblock instance
         */
        IMultiblock createMultiblock(ResourceLocation name, String[][] pattern, Object... rawMatchers);

        /**
         * Get all the active effect powders in the given area and consume the
         * position and the range that they have. To register a powder with the
         * supplied name, use {@link #EFFECT_POWDERS}
         *
         * @param level The level
         * @param area  The area to find powders in
         * @param name  The registry name of the powder
         * @return A list of powders' positions and ranges
         */
        List<Tuple<Vec3, Integer>> getActiveEffectPowders(Level level, AABB area, ResourceLocation name);

        /**
         * Returns true if there is an effect powder entity active anywhere
         * around the given position based on the radius it has. This is a
         * shorthand function of {@link #getActiveEffectPowders(Level,
         * net.minecraft.world.phys.AABB, ResourceLocation)} that returns true
         * if the list is non-empty
         *
         * @param level The level
         * @param pos   The center position
         * @param name  The registry name of the powder
         * @return If the effect is currently inhibited by any inhibitors
         */
        boolean isEffectPowderActive(Level level, BlockPos pos, ResourceLocation name);

        /**
         * @see IAuraChunk#getSpotsInArea(Level, BlockPos, int, BiConsumer)
         */
        void getAuraSpotsInArea(Level level, BlockPos pos, int radius, BiConsumer<BlockPos, Integer> consumer);

        /**
         * @see IAuraChunk#getSpotAmountInArea(Level, BlockPos, int)
         */
        int getSpotAmountInArea(Level level, BlockPos pos, int radius);

        /**
         * @see IAuraChunk#getAuraInArea(Level, BlockPos, int)
         */
        int getAuraInArea(Level level, BlockPos pos, int radius);

        /**
         * @see IAuraChunk#getAuraAndSpotAmountInArea(Level, BlockPos, int)
         */
        Pair<Integer, Integer> getAuraAndSpotAmountInArea(Level level, BlockPos pos, int radius);

        /**
         * @see IAuraChunk#triangulateAuraInArea(Level, BlockPos, int)
         */
        int triangulateAuraInArea(Level level, BlockPos pos, int radius);

        /**
         * @see IAuraChunk#getLowestSpot(Level, BlockPos, int, BlockPos)
         */
        BlockPos getLowestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot);

        /**
         * @see IAuraChunk#getHighestSpot(Level, BlockPos, int, BlockPos)
         */
        BlockPos getHighestAuraDrainSpot(Level level, BlockPos pos, int radius, BlockPos defaultSpot);
    }

}
