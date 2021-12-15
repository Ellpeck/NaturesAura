package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClient {

    private int type;
    private CompoundTag data;

    public PacketClient(int type, CompoundTag data) {
        this.type = type;
        this.data = data;
    }

    private PacketClient() {

    }

    public static PacketClient fromBytes(FriendlyByteBuf buf) {
        var client = new PacketClient();
        client.type = buf.readByte();
        client.data = buf.readNbt();
        return client;
    }

    public static void toBytes(PacketClient packet, FriendlyByteBuf buf) {
        buf.writeByte(packet.type);
        buf.writeNbt(packet.data);
    }

    // lambda causes classloading issues on a server here
    @SuppressWarnings("Convert2Lambda")
    public static void onMessage(PacketClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(new Runnable() {
            @Override
            public void run() {
                var mc = Minecraft.getInstance();
                if (mc.level != null) {
                    switch (message.type) {
                        case 0: // dimension rail visualization
                            var goalDim = new ResourceLocation(message.data.getString("dim"));
                            var goalPos = BlockPos.of(message.data.getLong("pos"));
                            ItemRangeVisualizer.visualize(mc.player, ItemRangeVisualizer.VISUALIZED_RAILS, goalDim, goalPos);
                        case 1:
                            var entity = mc.level.getEntity(message.data.getInt("id"));
                            mc.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                            mc.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                            if (entity == mc.player) {
                                mc.gameRenderer.displayItemActivation(new ItemStack(ModItems.DEATH_RING));
                            }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
