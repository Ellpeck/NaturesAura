package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketParticleStream {

    private float startX;
    private float startY;
    private float startZ;

    private float endX;
    private float endY;
    private float endZ;

    private float speed;
    private int color;
    private float scale;

    public PacketParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
        this.speed = speed;
        this.color = color;
        this.scale = scale;
    }

    private PacketParticleStream() {
    }

    public static PacketParticleStream fromBytes(PacketBuffer buf) {
        PacketParticleStream packet = new PacketParticleStream();

        packet.startX = buf.readFloat();
        packet.startY = buf.readFloat();
        packet.startZ = buf.readFloat();
        packet.endX = buf.readFloat();
        packet.endY = buf.readFloat();
        packet.endZ = buf.readFloat();
        packet.speed = buf.readFloat();
        packet.color = buf.readInt();
        packet.scale = buf.readFloat();

        return packet;
    }

    public static void toBytes(PacketParticleStream packet, PacketBuffer buf) {
        buf.writeFloat(packet.startX);
        buf.writeFloat(packet.startY);
        buf.writeFloat(packet.startZ);
        buf.writeFloat(packet.endX);
        buf.writeFloat(packet.endY);
        buf.writeFloat(packet.endZ);
        buf.writeFloat(packet.speed);
        buf.writeInt(packet.color);
        buf.writeFloat(packet.scale);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketParticleStream message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> NaturesAuraAPI.instance().spawnParticleStream(
                message.startX, message.startY, message.startZ,
                message.endX, message.endY, message.endZ,
                message.speed, message.color, message.scale));

        ctx.get().setPacketHandled(true);
    }
}
