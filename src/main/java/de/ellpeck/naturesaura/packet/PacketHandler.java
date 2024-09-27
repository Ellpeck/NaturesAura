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
    @SuppressWarnings("Convert2MethodRef")
    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NaturesAura.MOD_ID);
        registrar.playToClient(PacketAuraChunk.TYPE, PacketAuraChunk.CODEC, (m, c) -> PacketAuraChunk.onMessage(m, c));
        registrar.playToClient(PacketClient.TYPE, PacketClient.CODEC, (m, c) -> PacketClient.onMessage(m, c));
        registrar.playToClient(PacketParticles.TYPE, PacketParticles.CODEC, (m, c) -> PacketParticles.onMessage(m, c));
        registrar.playToClient(PacketParticleStream.TYPE, PacketParticleStream.CODEC, (m, c) -> PacketParticleStream.onMessage(m, c));
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
