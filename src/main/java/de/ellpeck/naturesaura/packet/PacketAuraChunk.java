package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import net.minecraft.client.Minecraft;
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

public class PacketAuraChunk implements IPacket {

    private int chunkX;
    private int chunkZ;
    private Map<BlockPos, MutableInt> drainSpots;

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

    public static class Handler {

        @OnlyIn(Dist.CLIENT)
        public static void onMessage(PacketAuraChunk message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                World world = Minecraft.getInstance().world;
                if (world != null) {
                    Chunk chunk = world.getChunk(message.chunkX, message.chunkZ);

                    if (chunk.getCapability(NaturesAuraAPI.capAuraChunk).isPresent()) {
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk).orElse(null);
                        auraChunk.setSpots(message.drainSpots);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
