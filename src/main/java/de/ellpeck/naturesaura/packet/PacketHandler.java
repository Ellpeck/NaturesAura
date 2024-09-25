package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class PacketHandler {

    @SubscribeEvent
    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NaturesAura.MOD_ID);
        registrar.playBidirectional(PacketAuraChunk.TYPE, PacketAuraChunk.CODEC, PacketAuraChunk::onMessage);
        registrar.playBidirectional(PacketClient.TYPE, PacketClient.CODEC, PacketClient::onMessage);
        registrar.playBidirectional(PacketParticles.TYPE, PacketParticles.CODEC, PacketParticles::onMessage);
        registrar.playBidirectional(PacketParticleStream.TYPE, PacketParticleStream.CODEC, PacketParticleStream::onMessage);
    }

    public static void sendToAllLoaded(Level level, BlockPos pos, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, new ChunkPos(pos), message);
    }

    public static void sendToAllAround(Level level, BlockPos pos, int range, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX(), pos.getY(), pos.getZ(), range, message);
    }

    public static void sendTo(Player player, CustomPacketPayload message) {
        ((ServerPlayer) player).connection.send(message);
    }

}
