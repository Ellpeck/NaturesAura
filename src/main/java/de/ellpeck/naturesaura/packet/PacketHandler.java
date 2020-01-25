package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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

    public static void sendToAllLoaded(World world, BlockPos pos, Object message) {
        network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static void sendToAllAround(IWorld world, BlockPos pos, int range, Object message) {
        network.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, world.getDimension().getType())), message);
    }

    public static void sendTo(PlayerEntity player, Object message) {
        network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
