package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketClient implements IMessage {

    private int type;
    private int[] data;

    public PacketClient(int type, int... data) {
        this.type = type;
        this.data = data;
    }

    public PacketClient() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readByte();
        this.data = new int[buf.readByte()];
        for (int i = 0; i < this.data.length; i++)
            this.data[i] = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.type);
        buf.writeByte(this.data.length);
        for (int i : this.data)
            buf.writeInt(i);
    }

    public static class Handler implements IMessageHandler<PacketClient, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketClient message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.world != null) {
                    switch (message.type) {
                        case 0: // dimension rail visualization
                            int goalDim = message.data[0];
                            BlockPos goalPos = new BlockPos(message.data[1], message.data[2], message.data[3]);
                            ItemRangeVisualizer.visualize(mc.player, ItemRangeVisualizer.VISUALIZED_RAILS, goalDim, goalPos);
                    }
                }
            });

            return null;
        }
    }
}