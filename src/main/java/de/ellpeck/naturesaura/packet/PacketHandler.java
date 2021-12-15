package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {

    private static final String VERSION = "1";
    private static SimpleChannel network;

    public static void init() {
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(NaturesAura.MOD_ID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);
        network.registerMessage(0, PacketParticleStream.class, PacketParticleStream::toBytes, PacketParticleStream::fromBytes, PacketParticleStream::onMessage);
        network.registerMessage(1, PacketParticles.class, PacketParticles::toBytes, PacketParticles::fromBytes, PacketParticles::onMessage);
        network.registerMessage(2, PacketAuraChunk.class, PacketAuraChunk::toBytes, PacketAuraChunk::fromBytes, PacketAuraChunk::onMessage);
        network.registerMessage(3, PacketClient.class, PacketClient::toBytes, PacketClient::fromBytes, PacketClient::onMessage);
    }

    public static void sendToAllLoaded(Level level, BlockPos pos, Object message) {
        network.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), message);
    }

    public static void sendToAllAround(Level level, BlockPos pos, int range, Object message) {
        network.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, level.dimension())), message);
    }

    public static void sendTo(Player player, Object message) {
        network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }
}
