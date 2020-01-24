package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunkProvider;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommonEvents {

    private static final Method GET_LOADED_CHUNKS_METHOD = ObfuscationReflectionHelper.findMethod(ChunkManager.class, "func_223491_f");

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        Chunk chunk = event.getObject();
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunkProvider(chunk));
    }

    @SubscribeEvent
    public void onWorldCapsAttach(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "data"), new WorldData());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.world.getGameTime() % 20 == 0) {
                event.world.getProfiler().startSection(NaturesAura.MOD_ID + ":onWorldTick");
                try {
                    ChunkManager manager = ((ServerChunkProvider) event.world.getChunkProvider()).chunkManager;
                    Iterable<ChunkHolder> chunks = (Iterable<ChunkHolder>) GET_LOADED_CHUNKS_METHOD.invoke(manager);
                    for (ChunkHolder holder : chunks) {
                        Chunk chunk = holder.func_219298_c();
                        if (chunk == null)
                            continue;
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
                        if (auraChunk != null)
                            auraChunk.update();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    NaturesAura.LOGGER.fatal(e);
                }
                event.world.getProfiler().endSection();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.player.world.getGameTime() % 200 != 0)
                return;

            int aura = IAuraChunk.triangulateAuraInArea(event.player.world, event.player.getPosition(), 25);
            if (aura <= 0)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "negative_imbalance"), "triggered_in_code");
            else if (aura >= 1500000)
                Helper.addAdvancement(event.player, new ResourceLocation(NaturesAura.MOD_ID, "positive_imbalance"), "triggered_in_code");
        }
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.getWorld().getChunk(event.getPos().x, event.getPos().z);
        if (!chunk.getWorld().isRemote) {
            AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
            if (auraChunk != null)
                PacketHandler.sendTo(event.getPlayer(), auraChunk.makePacket());
        }
    }
}
