package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;

public class PacketAuraChunk implements IMessage {

    private int chunkX;
    private int chunkZ;
    private Map<BlockPos, MutableInt> drainSpots;

    public PacketAuraChunk(int chunkX, int chunkZ, Map<BlockPos, MutableInt> drainSpots) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.drainSpots = drainSpots;
    }

    public PacketAuraChunk() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();

        this.drainSpots = new HashMap<>();
        int amount = buf.readInt();
        for (int i = 0; i < amount; i++) {
            this.drainSpots.put(
                    BlockPos.fromLong(buf.readLong()),
                    new MutableInt(buf.readInt())
            );
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);

        buf.writeInt(this.drainSpots.size());
        for (Map.Entry<BlockPos, MutableInt> entry : this.drainSpots.entrySet()) {
            buf.writeLong(entry.getKey().toLong());
            buf.writeInt(entry.getValue().intValue());
        }
    }

    public static class Handler implements IMessageHandler<PacketAuraChunk, IMessage> {

        @Override
        @OnlyIn(Dist.CLIENT)
        public IMessage onMessage(PacketAuraChunk message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world != null) {
                    Chunk chunk = world.getChunk(message.chunkX, message.chunkZ);
                    if (chunk.hasCapability(NaturesAuraAPI.capAuraChunk, null)) {
                        AuraChunk auraChunk = (AuraChunk) chunk.getCapability(NaturesAuraAPI.capAuraChunk, null);
                        auraChunk.setSpots(message.drainSpots);
                    }
                }
            });

            return null;
        }
    }
}