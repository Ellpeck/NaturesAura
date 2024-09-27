package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

public record PacketParticleStream(Vector3f start, Vector3f end, float speed, int color, float scale) implements CustomPacketPayload {

    public static final Type<PacketParticleStream> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "particle_stream"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketParticleStream> CODEC = StreamCodec.composite(
        ByteBufCodecs.VECTOR3F, PacketParticleStream::start,
        ByteBufCodecs.VECTOR3F, PacketParticleStream::end,
        ByteBufCodecs.FLOAT, PacketParticleStream::speed,
        ByteBufCodecs.INT, PacketParticleStream::color,
        ByteBufCodecs.FLOAT, PacketParticleStream::scale,
        PacketParticleStream::new);

    public PacketParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {
        this(new Vector3f(startX, startY, startZ), new Vector3f(endX, endY, endZ), speed, color, scale);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PacketParticleStream.TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketParticleStream message, IPayloadContext ctx) {
        NaturesAuraAPI.instance().spawnParticleStream(
            message.start.x, message.start.y, message.start.z,
            message.end.x, message.end.y, message.end.z,
            message.speed, message.color, message.scale);
    }

}
