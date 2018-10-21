package de.ellpeck.naturesaura.events;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

public class CommonEvents {

    @SubscribeEvent
    public void onChunkCapsAttach(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(new ResourceLocation(NaturesAura.MOD_ID, "aura"), new AuraChunk(event.getObject()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            if (event.world.getTotalWorldTime() % 20 == 0) {
                Iterator<Chunk> chunks = event.world.getPersistentChunkIterable(((WorldServer) event.world).getPlayerChunkMap().getChunkIterator());
                while (chunks.hasNext()) {
                    Chunk chunk = chunks.next();
                    if (chunk.hasCapability(Capabilities.auraChunk, null)) {
                        AuraChunk auraChunk = chunk.getCapability(Capabilities.auraChunk, null);
                        auraChunk.update();
                    }
                }
            }
        }
    }
}
