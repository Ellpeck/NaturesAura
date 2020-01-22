package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class PacketHandler {

    private static String version = "1";
    private static SimpleChannel network;

    public static void init() {
        network = NetworkRegistry.newSimpleChannel(
                NaturesAura.createRes("network"),
                () -> version,
                version::equals,
                version::equals);
        network.registerMessage(0, PacketParticleStream.class, PacketParticleStream::toBytes, PacketParticleStream::fromBytes, PacketParticleStream.Handler::onMessage);
        network.registerMessage(1, PacketParticles.class, PacketParticles::toBytes, PacketParticles::fromBytes, PacketParticles.Handler::onMessage);
        network.registerMessage(2, PacketAuraChunk.class, PacketAuraChunk::toBytes, PacketAuraChunk::fromBytes, PacketAuraChunk.Handler::onMessage);
        network.registerMessage(3, PacketClient.class, PacketClient::toBytes, PacketClient::fromBytes, PacketClient.Handler::onMessage);
    }

    @Deprecated
    public static void sendToAllLoaded(World world, BlockPos pos, IPacket message) {
        sendToAllLoaded(message);
    }

    public static void sendToAllLoaded(IPacket message) {
        network.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToAllAround(IWorld world, BlockPos pos, int range, IPacket message) {
        network.send(PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, world.getDimension().getType())),
                message);
    }

    public static void sendTo(PlayerEntity player, IPacket message) {
        network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
