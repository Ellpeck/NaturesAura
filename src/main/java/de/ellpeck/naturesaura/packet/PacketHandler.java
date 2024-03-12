package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

@EventBusSubscriber(bus = Bus.MOD)
public final class PacketHandler {

    @SubscribeEvent
    public static void onPayloadRegister(RegisterPayloadHandlerEvent event) {
        var registrar = event.registrar(NaturesAura.MOD_ID);
        registrar.play(PacketAuraChunk.ID, PacketAuraChunk::new, PacketAuraChunk::onMessage);
        registrar.play(PacketClient.ID, PacketClient::new, PacketClient::onMessage);
        registrar.play(PacketParticles.ID, PacketParticles::new, PacketParticles::onMessage);
        registrar.play(PacketParticleStream.ID, PacketParticleStream::new, PacketParticleStream::onMessage);
    }

    public static void sendToAllLoaded(Level level, BlockPos pos, CustomPacketPayload message) {
        PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(pos)).send(message);
    }

    public static void sendToAllAround(Level level, BlockPos pos, int range, CustomPacketPayload message) {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, level.dimension())).send(message);
    }

    public static void sendTo(Player player, CustomPacketPayload message) {
        ((ServerPlayer) player).connection.send(message);
    }

}
