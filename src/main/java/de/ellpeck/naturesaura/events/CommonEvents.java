package de.ellpeck.naturesaura.events;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunkProvider;
import de.ellpeck.naturesaura.commands.CommandAura;
import de.ellpeck.naturesaura.gen.ModFeatures;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class CommonEvents {

    private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks");
    private static final ListMultimap<UUID, ChunkPos> PENDING_AURA_CHUNKS = ArrayListMultimap.create();

    @SubscribeEvent
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (ModConfig.instance.auraBlooms.get()) {
            // TODO features might have to be registered *AGAIN* now because .placed() is another thing again oh my God
            event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Configured.AURA_BLOOM.placed());
            switch (event.getCategory()) {
                case DESERT -> event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Configured.AURA_CACTUS.placed());
                case NETHER -> {
                    event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Configured.CRIMSON_AURA_MUSHROOM.placed());
                    event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Configured.WARPED_AURA_MUSHROOM.placed());
                }
                case MUSHROOM -> event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Configured.AURA_MUSHROOM.placed());
            }
        }
    }

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk chunk = event.getObject();
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunkProvider(chunk));
    }

    @SubscribeEvent
    public void onLevelCapsAttach(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "data"), new LevelData());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        ChunkAccess iChunk = event.getChunk();
        if (iChunk instanceof LevelChunk chunk) {
            IAuraChunk auraChunk = chunk.getCapability(NaturesAuraAPI.capAuraChunk).orElse(null);
            if (auraChunk instanceof AuraChunk) {
                LevelData data = (LevelData) ILevelData.getLevelData(chunk.getLevel());
                data.auraChunksWithSpots.remove(chunk.getPos().toLong());
            }
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide)
            return;
        ItemStack held = event.getItemStack();
        if (!held.isEmpty() && held.getItem().getRegistryName().getPath().contains("chisel")) {
            BlockState state = player.level.getBlockState(event.getPos());
            if (NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.containsKey(state)) {
                LevelData data = (LevelData) ILevelData.getLevelData(player.level);
                data.addMossStone(event.getPos());
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onLevelTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isClientSide && event.phase == TickEvent.Phase.END) {
            if (event.world.getGameTime() % 20 == 0) {
                event.world.getProfiler().push(NaturesAura.MOD_ID + ":onLevelTick");
                try {
                    ChunkMap manager = ((ServerChunkCache) event.world.getChunkSource()).chunkMap;
                    Iterable<ChunkHolder> chunks = (Iterable<ChunkHolder>) GET_LOADED_CHUNKS_METHOD.invoke(manager);
                    for (ChunkHolder holder : chunks) {
                        LevelChunk chunk = holder.getTickingChunk();
                        if (chunk == null)
                            continue;
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
                        if (auraChunk != null)
                            auraChunk.update();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    NaturesAura.LOGGER.fatal(e);
                }
                event.world.getProfiler().pop();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && event.phase == TickEvent.Phase.END) {
            if (event.player.level.getGameTime() % 10 == 0) {
                List<ChunkPos> pending = PENDING_AURA_CHUNKS.get(event.player.getUUID());
                pending.removeIf(p -> this.handleChunkWatchDeferred(event.player, p));
            }

            if (event.player.level.getGameTime() % 200 != 0)
                return;

            int aura = IAuraChunk.triangulateAuraInArea(event.player.level, event.player.blockPosition(), 25);
            if (aura <= 0)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "negative_imbalance"), "triggered_in_code");
            else if (aura >= 1500000)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "positive_imbalance"), "triggered_in_code");
        }
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        PENDING_AURA_CHUNKS.put(event.getPlayer().getUUID(), event.getPos());
    }

    private boolean handleChunkWatchDeferred(Player player, ChunkPos pos) {
        LevelChunk chunk = Helper.getLoadedChunk(player.level, pos.x, pos.z);
        if (chunk == null)
            return false;
        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
        if (auraChunk == null)
            return false;
        PacketHandler.sendTo(player, auraChunk.makePacket());
        return true;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        CommandAura.register(event.getServer().getCommands().getDispatcher());
    }
}
