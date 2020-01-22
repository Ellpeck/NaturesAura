package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.items.RangeVisualizer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClient implements IPacket {

    private int type;
    private int[] data;

    public static PacketClient fromBytes(PacketBuffer buf) {
        PacketClient client = new PacketClient();
        client.type = buf.readByte();
        client.data = new int[buf.readByte()];
        for (int i = 0; i < client.data.length; i++)
            client.data[i] = buf.readInt();

        return client;
    }

    public static void toBytes(PacketClient packet, PacketBuffer buf) {
        buf.writeByte(packet.type);
        buf.writeByte(packet.data.length);
        for (int i : packet.data)
            buf.writeInt(i);
    }

    public static class Handler {

        @OnlyIn(Dist.CLIENT)
        public static void onMessage(PacketClient message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.world != null) {
                    switch (message.type) {
                        case 0: // dimension rail visualization
                            int goalDim = message.data[0];
                            BlockPos goalPos = new BlockPos(message.data[1], message.data[2], message.data[3]);
                            RangeVisualizer.visualize(mc.player, RangeVisualizer.VISUALIZED_RAILS, DimensionType.getById(goalDim), goalPos);
                    }
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
