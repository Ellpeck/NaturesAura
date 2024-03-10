package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.events.ClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

public class PacketAuraChunk implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NaturesAura.MOD_ID, "aura_chunk");

    private final int chunkX;
    private final int chunkZ;
    private final Collection<AuraChunk.DrainSpot> drainSpots;

    public PacketAuraChunk(int chunkX, int chunkZ, Collection<AuraChunk.DrainSpot> drainSpots) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.drainSpots = drainSpots;
    }

    public PacketAuraChunk(FriendlyByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();

        this.drainSpots = new ArrayList<>();
        var amount = buf.readInt();
        for (var i = 0; i < amount; i++)
            this.drainSpots.add(new AuraChunk.DrainSpot(buf.readNbt()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);

        buf.writeInt(this.drainSpots.size());
        for (var entry : this.drainSpots)
            buf.writeNbt(entry.serializeNBT());
    }

    @Override
    public ResourceLocation id() {
        return PacketAuraChunk.ID;
    }

    public static void onMessage(PacketAuraChunk message, PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> ClientEvents.PENDING_AURA_CHUNKS.add(message));
    }

    public boolean tryHandle(Level level) {
        try {
            var chunk = level.getChunk(this.chunkX, this.chunkZ);
            if (chunk.isEmpty())
                return false;
            var auraChunk = (AuraChunk) chunk.getData(NaturesAuraAPI.AURA_CHUNK_ATTACHMENT);
            if (auraChunk == null)
                return false;
            auraChunk.setSpots(this.drainSpots);
            return true;
        } catch (Exception e) {
            NaturesAura.LOGGER.error("There was an error handling an aura chunk packet", e);
            return true;
        }
    }

}
