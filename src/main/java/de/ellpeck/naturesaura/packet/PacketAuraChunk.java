package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.events.ClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class PacketAuraChunk {

    private int chunkX;
    private int chunkZ;
    private Collection<AuraChunk.DrainSpot> drainSpots;

    public PacketAuraChunk(int chunkX, int chunkZ, Collection<AuraChunk.DrainSpot> drainSpots) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.drainSpots = drainSpots;
    }

    private PacketAuraChunk() {
    }

    public static PacketAuraChunk fromBytes(FriendlyByteBuf buf) {
        var packet = new PacketAuraChunk();
        packet.chunkX = buf.readInt();
        packet.chunkZ = buf.readInt();

        packet.drainSpots = new ArrayList<>();
        var amount = buf.readInt();
        for (var i = 0; i < amount; i++)
            packet.drainSpots.add(new AuraChunk.DrainSpot(buf.readNbt()));

        return packet;
    }

    public static void toBytes(PacketAuraChunk packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.chunkX);
        buf.writeInt(packet.chunkZ);

        buf.writeInt(packet.drainSpots.size());
        for (var entry : packet.drainSpots)
            buf.writeNbt(entry.serializeNBT());
    }

    public static void onMessage(PacketAuraChunk message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientEvents.PENDING_AURA_CHUNKS.add(message));
        ctx.get().setPacketHandled(true);
    }

    public boolean tryHandle(Level level) {
        try {
            var chunk = level.getChunk(this.chunkX, this.chunkZ);
            if (chunk.isEmpty())
                return false;
            var auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.CAP_AURA_CHUNK).orElse(null);
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
