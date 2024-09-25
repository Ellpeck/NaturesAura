package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.events.ClientEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

public record PacketAuraChunk(int chunkX, int chunkZ, Collection<CompoundTag> drainSpots) implements CustomPacketPayload {

    public static final Type<PacketAuraChunk> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "aura_chunk"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAuraChunk> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PacketAuraChunk::chunkX,
        ByteBufCodecs.INT, PacketAuraChunk::chunkZ,
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.COMPOUND_TAG), PacketAuraChunk::drainSpots,
        PacketAuraChunk::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PacketAuraChunk.TYPE;
    }

    public static void onMessage(PacketAuraChunk message, IPayloadContext ctx) {
        ClientEvents.PENDING_AURA_CHUNKS.add(message);
    }

    public boolean tryHandle(Level level) {
        try {
            var chunk = level.getChunk(this.chunkX, this.chunkZ);
            if (chunk.isEmpty())
                return false;
            var auraChunk = (AuraChunk) chunk.getData(NaturesAuraAPI.AURA_CHUNK_ATTACHMENT);
            auraChunk.setSpots(this.drainSpots);
            return true;
        } catch (Exception e) {
            NaturesAura.LOGGER.error("There was an error handling an aura chunk packet", e);
            return true;
        }
    }

}
