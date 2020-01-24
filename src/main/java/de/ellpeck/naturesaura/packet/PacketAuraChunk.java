package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.events.ClientEvents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketAuraChunk {

    private int chunkX;
    private int chunkZ;
    private Map<BlockPos, MutableInt> drainSpots;

    public PacketAuraChunk(int chunkX, int chunkZ, Map<BlockPos, MutableInt> drainSpots) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.drainSpots = drainSpots;
    }

    private PacketAuraChunk() {
    }

    public static PacketAuraChunk fromBytes(PacketBuffer buf) {
        PacketAuraChunk packet = new PacketAuraChunk();
        packet.chunkX = buf.readInt();
        packet.chunkZ = buf.readInt();

        packet.drainSpots = new HashMap<>();
        int amount = buf.readInt();
        for (int i = 0; i < amount; i++) {
            packet.drainSpots.put(
                    BlockPos.fromLong(buf.readLong()),
                    new MutableInt(buf.readInt())
            );
        }

        return packet;
    }

    public static void toBytes(PacketAuraChunk packet, PacketBuffer buf) {
        buf.writeInt(packet.chunkX);
        buf.writeInt(packet.chunkZ);

        buf.writeInt(packet.drainSpots.size());
        for (Map.Entry<BlockPos, MutableInt> entry : packet.drainSpots.entrySet()) {
            buf.writeLong(entry.getKey().toLong());
            buf.writeInt(entry.getValue().intValue());
        }
    }

    public boolean tryHandle(World world) {
        Chunk chunk = world.getChunk(this.chunkX, this.chunkZ);
        if (chunk.isEmpty())
            return false;
        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk).orElse(null);
        if (auraChunk == null)
            return false;
        auraChunk.setSpots(this.drainSpots);
        return true;
    }

    public static void onMessage(PacketAuraChunk message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientEvents.PENDING_AURA_CHUNKS.add(message));
        ctx.get().setPacketHandled(true);
    }
}
