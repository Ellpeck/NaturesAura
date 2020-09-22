package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketClient {
    private int type;
    private CompoundNBT data;

    public PacketClient(int type, CompoundNBT data) {
        this.type = type;
        this.data = data;
    }

    private PacketClient() {

    }

    public static PacketClient fromBytes(PacketBuffer buf) {
        PacketClient client = new PacketClient();
        client.type = buf.readByte();
        client.data = buf.readCompoundTag();
        return client;
    }

    public static void toBytes(PacketClient packet, PacketBuffer buf) {
        buf.writeByte(packet.type);
        buf.writeCompoundTag(packet.data);
    }

    // lambda causes classloading issues on a server here
    @SuppressWarnings("Convert2Lambda")
    public static void onMessage(PacketClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(new Runnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.world != null) {
                    switch (message.type) {
                        case 0: // dimension rail visualization
                            ResourceLocation goalDim = new ResourceLocation(message.data.getString("dim"));
                            BlockPos goalPos = BlockPos.fromLong(message.data.getLong("pos"));
                            ItemRangeVisualizer.visualize(mc.player, ItemRangeVisualizer.VISUALIZED_RAILS, goalDim, goalPos);
                        case 1:
                            Entity entity = mc.world.getEntityByID(message.data.getInt("id"));
                            mc.particles.emitParticleAtEntity(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                            mc.world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
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
