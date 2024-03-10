package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PacketParticleStream implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NaturesAura.MOD_ID, "particle_stream");

    private final float startX;
    private final float startY;
    private final float startZ;

    private final float endX;
    private final float endY;
    private final float endZ;

    private final float speed;
    private final int color;
    private final float scale;

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

    public PacketParticleStream(FriendlyByteBuf buf) {
        this.startX = buf.readFloat();
        this.startY = buf.readFloat();
        this.startZ = buf.readFloat();
        this.endX = buf.readFloat();
        this.endY = buf.readFloat();
        this.endZ = buf.readFloat();
        this.speed = buf.readFloat();
        this.color = buf.readInt();
        this.scale = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.startX);
        buf.writeFloat(this.startY);
        buf.writeFloat(this.startZ);
        buf.writeFloat(this.endX);
        buf.writeFloat(this.endY);
        buf.writeFloat(this.endZ);
        buf.writeFloat(this.speed);
        buf.writeInt(this.color);
        buf.writeFloat(this.scale);
    }

    @Override
    public ResourceLocation id() {
        return PacketParticleStream.ID;
    }

    public static void onMessage(PacketParticleStream message, PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> NaturesAuraAPI.instance().spawnParticleStream(
                message.startX, message.startY, message.startZ,
                message.endX, message.endY, message.endZ,
                message.speed, message.color, message.scale));
    }

}
