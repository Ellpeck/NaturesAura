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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
import java.util.UUID;

import static net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class CommonEvents {

    private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkMap.class, "m_140416_");
    private static final ListMultimap<UUID, ChunkPos> PENDING_AURA_CHUNKS = ArrayListMultimap.create();

    @SubscribeEvent
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (ModConfig.instance.auraBlooms.get()) {
            event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Placed.AURA_BLOOM);
            switch (event.getCategory()) {
                case DESERT -> event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Placed.AURA_CACTUS);
                case NETHER -> {
                    event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CRIMSON_AURA_MUSHROOM);
                    event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Placed.WARPED_AURA_MUSHROOM);
                }
                case MUSHROOM -> event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, ModFeatures.Placed.AURA_MUSHROOM);
            }
        }
    }

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<LevelChunk> event) {
        var chunk = event.getObject();
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunkProvider(chunk));
    }

    @SubscribeEvent
    public void onLevelCapsAttach(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "data"), new LevelData());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        var iChunk = event.getChunk();
        if (iChunk instanceof LevelChunk chunk) {
            var auraChunk = chunk.getCapability(NaturesAuraAPI.CAP_AURA_CHUNK).orElse(null);
            if (auraChunk instanceof AuraChunk) {
                var data = (LevelData) ILevelData.getLevelData(chunk.getLevel());
                data.auraChunksWithSpots.remove(chunk.getPos().toLong());
            }
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getPlayer();
        if (player.level.isClientSide)
            return;
        var held = event.getItemStack();
        if (!held.isEmpty() && held.getItem().getRegistryName().getPath().contains("chisel")) {
            var state = player.level.getBlockState(event.getPos());
            if (NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.containsKey(state)) {
                var data = (LevelData) ILevelData.getLevelData(player.level);
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
                    var manager = ((ServerChunkCache) event.world.getChunkSource()).chunkMap;
                    var chunks = (Iterable<ChunkHolder>) GET_LOADED_CHUNKS_METHOD.invoke(manager);
                    for (var holder : chunks) {
                        var chunk = holder.getTickingChunk();
                        if (chunk == null)
                            continue;
                        var auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.CAP_AURA_CHUNK, null).orElse(null);
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
                var pending = PENDING_AURA_CHUNKS.get(event.player.getUUID());
                pending.removeIf(p -> this.handleChunkWatchDeferred(event.player, p));
            }

            if (event.player.level.getGameTime() % 200 != 0)
                return;

            var aura = IAuraChunk.triangulateAuraInArea(event.player.level, event.player.blockPosition(), 25);
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
        var chunk = Helper.getLoadedChunk(player.level, pos.x, pos.z);
        if (!(chunk instanceof LevelChunk levelChunk))
            return false;
        var auraChunk = (AuraChunk) levelChunk.getCapability(NaturesAuraAPI.CAP_AURA_CHUNK, null).orElse(null);
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
