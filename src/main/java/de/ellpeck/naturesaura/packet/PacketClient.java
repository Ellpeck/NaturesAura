package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ItemRangeVisualizer;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PacketClient implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NaturesAura.MOD_ID, "client");

    private final int type;
    private final CompoundTag data;

    public PacketClient(int type, CompoundTag data) {
        this.type = type;
        this.data = data;
    }

    public PacketClient(FriendlyByteBuf buf) {
        this.type = buf.readByte();
        this.data = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.type);
        buf.writeNbt(this.data);
    }

    @Override
    public ResourceLocation id() {
        return PacketClient.ID;
    }

    public static void onMessage(PacketClient message, PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
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
        });
    }

}
