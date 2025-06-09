package de.ellpeck.naturesaura.events;

import com.google.common.collect.*;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.commands.CommandAura;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CommonEvents {

    private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkMap.class, "getChunks");
    private static final SetMultimap<UUID, ChunkPos> PENDING_AURA_CHUNKS = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        var iChunk = event.getChunk();
        if (iChunk instanceof LevelChunk chunk) {
            var auraChunk = chunk.getData(NaturesAuraAPI.AURA_CHUNK_ATTACHMENT);
            if (auraChunk instanceof AuraChunk) {
                var data = (LevelData) ILevelData.getLevelData(chunk.getLevel());
                data.auraChunksWithSpots.remove(chunk.getPos().toLong());
            }
        }
        CommonEvents.PENDING_AURA_CHUNKS.values().remove(iChunk.getPos());
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getEntity();
        if (player.level().isClientSide)
            return;
        var held = event.getItemStack();
        if (!held.isEmpty() && BuiltInRegistries.ITEM.getKey(held.getItem()).getPath().contains("chisel")) {
            var state = player.level().getBlockState(event.getPos());
            if (NaturesAuraAPI.BOTANIST_PICKAXE_CONVERSIONS.containsKey(state)) {
                var data = (LevelData) ILevelData.getLevelData(player.level());
                data.addMossStone(event.getPos());
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide) {
            if (event.getLevel().getGameTime() % 20 == 0) {
                event.getLevel().getProfiler().push(NaturesAura.MOD_ID + ":onLevelTick");
                try {
                    var manager = ((ServerChunkCache) event.getLevel().getChunkSource()).chunkMap;
                    var chunks = (Iterable<ChunkHolder>) CommonEvents.GET_LOADED_CHUNKS_METHOD.invoke(manager);
                    for (var holder : chunks) {
                        var chunk = holder.getTickingChunk();
                        if (chunk == null)
                            continue;
                        var auraChunk = (AuraChunk) chunk.getData(NaturesAuraAPI.AURA_CHUNK_ATTACHMENT);
                        if (auraChunk != null)
                            auraChunk.update();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    NaturesAura.LOGGER.fatal(e);
                }
                event.getLevel().getProfiler().pop();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {
            if (event.getEntity().level().getGameTime() % 10 == 0) {
                var pending = CommonEvents.PENDING_AURA_CHUNKS.get(event.getEntity().getUUID());
                pending.removeIf(p -> this.handleChunkWatchDeferred(event.getEntity(), p));
            }

            if (event.getEntity().level().getGameTime() % 200 != 0)
                return;

            var aura = IAuraChunk.triangulateAuraInArea(event.getEntity().level(), event.getEntity().blockPosition(), 25);
            if (aura <= 0)
                Helper.addAdvancement(event.getEntity(), ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "negative_imbalance"), "triggered_in_code");
            else if (aura >= 1500000)
                Helper.addAdvancement(event.getEntity(), ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "positive_imbalance"), "triggered_in_code");
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        CommonEvents.PENDING_AURA_CHUNKS.removeAll(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        CommonEvents.PENDING_AURA_CHUNKS.put(event.getPlayer().getUUID(), event.getPos());
    }

    private boolean handleChunkWatchDeferred(Player player, ChunkPos pos) {
        var chunk = Helper.getLoadedChunk(player.level(), pos.x, pos.z);
        if (!(chunk instanceof LevelChunk levelChunk))
            return false;
        var auraChunk = (AuraChunk) levelChunk.getData(NaturesAuraAPI.AURA_CHUNK_ATTACHMENT);
        if (auraChunk == null)
            return false;
        PacketHandler.sendTo(player, auraChunk.makePacket());
        return true;
    }

    @SubscribeEvent
    public void onCommands(RegisterCommandsEvent event) {
        CommandAura.register(event.getDispatcher());
    }

}
