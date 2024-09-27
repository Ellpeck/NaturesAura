package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record PacketClient(int packetType, CompoundTag data) implements CustomPacketPayload {

    public static final Type<PacketClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketClient> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PacketClient::packetType,
        ByteBufCodecs.COMPOUND_TAG, PacketClient::data,
        PacketClient::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PacketClient.TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketClient message, IPayloadContext ctx) {
        var mc = Minecraft.getInstance();
        if (mc.level != null) {
            switch (message.packetType) {
                case 0: // dimension rail visualization
                    var goalDim = ResourceLocation.parse(message.data.getString("dim"));
                    var goalPos = BlockPos.of(message.data.getLong("pos"));
                    ItemRangeVisualizer.visualize(mc.player, ItemRangeVisualizer.VISUALIZED_RAILS, goalDim, goalPos);
                case 1:
                    var entity = mc.level.getEntity(message.data.getInt("id"));
                    mc.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    mc.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                    if (entity == mc.player)
                        mc.gameRenderer.displayItemActivation(new ItemStack(ModItems.DEATH_RING));
            }
        }
    }

}
