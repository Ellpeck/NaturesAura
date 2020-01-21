package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunkProvider;
import de.ellpeck.naturesaura.misc.WorldData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
            if (event.world.getGameTime() % 20 == 0) {
                // TODO update loaded aura chunks
                /*Iterator<Chunk> chunks = event.world.getPersistentChunkIterable(((ServerWorld) event.world).getPlayerChunkMap().getChunkIterator());
                while (chunks.hasNext()) {
                    Chunk chunk = chunks.next();
                    AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null).orElse(null);
                    auraChunk.update();
                }*/
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
            // TODO packets
            /*if (auraChunk != null)
                PacketHandler.sendTo(event.getPlayer(), auraChunk.makePacket());*/
        }
    }

    // TODO config
   /* @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (NaturesAura.MOD_ID.equals(event.getModID())) {
            ConfigManager.sync(NaturesAura.MOD_ID, Config.Type.INSTANCE);
            ModConfig.initOrReload(true);
        }
    }*/
}
