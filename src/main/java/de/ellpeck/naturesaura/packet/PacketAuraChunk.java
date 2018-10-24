package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.aura.Capabilities;
import de.ellpeck.naturesaura.aura.chunk.AuraChunk;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketAuraChunk message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world != null) {
                    Chunk chunk = world.getChunk(message.chunkX, message.chunkZ);
                    if (chunk.hasCapability(Capabilities.auraChunk, null)) {
                        AuraChunk auraChunk = chunk.getCapability(Capabilities.auraChunk, null);
                        auraChunk.setSpots(message.drainSpots);
                    }
                }
            });

            return null;
        }
    }
}