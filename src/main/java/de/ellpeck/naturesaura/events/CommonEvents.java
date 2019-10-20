package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunkProvider;
import de.ellpeck.naturesaura.misc.WorldData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

public class CommonEvents {

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
            if (event.world.getTotalWorldTime() % 20 == 0) {
                event.world.profiler.func_194340_a(() -> NaturesAura.MOD_ID + ":onWorldTick");
                Iterator<Chunk> chunks = event.world.getPersistentChunkIterable(((ServerWorld) event.world).getPlayerChunkMap().getChunkIterator());
                while (chunks.hasNext()) {
                    Chunk chunk = chunks.next();
                    if (chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
                        auraChunk.update();
                    }
                }
                event.world.profiler.endSection();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.player.world.getTotalWorldTime() % 200 != 0)
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
        Chunk chunk = event.getChunkInstance();
        if (!chunk.getWorld().isRemote && chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
            AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
            PacketHandler.sendTo(event.getPlayer(), auraChunk.makePacket());
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (NaturesAura.MOD_ID.equals(event.getModID())) {
            ConfigManager.sync(NaturesAura.MOD_ID, Config.Type.INSTANCE);
            ModConfig.initOrReload(true);
        }
    }
}
