package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticles implements IMessage {

    private float posX;
    private float posY;
    private float posZ;
    private float motionX;
    private float motionY;
    private float motionZ;
    private int color;
    private float scale;
    private int maxAge;
    private float gravity;
    private boolean collision;
    private boolean fade;

    public PacketParticles(float posX, float posY, float posZ, float motionX, float motionY, float motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.color = color;
        this.scale = scale;
        this.maxAge = maxAge;
        this.gravity = gravity;
        this.collision = collision;
        this.fade = fade;
    }

    public PacketParticles() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.motionX = buf.readFloat();
        this.motionY = buf.readFloat();
        this.motionZ = buf.readFloat();
        this.color = buf.readInt();
        this.scale = buf.readFloat();
        this.maxAge = buf.readInt();
        this.gravity = buf.readFloat();
        this.collision = buf.readBoolean();
        this.fade = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.posX);
        buf.writeFloat(this.posY);
        buf.writeFloat(this.posZ);
        buf.writeFloat(this.motionX);
        buf.writeFloat(this.motionY);
        buf.writeFloat(this.motionZ);
        buf.writeInt(this.color);
        buf.writeFloat(this.scale);
        buf.writeInt(this.maxAge);
        buf.writeFloat(this.gravity);
        buf.writeBoolean(this.collision);
        buf.writeBoolean(this.fade);
    }

    public static class Handler implements IMessageHandler<PacketParticles, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketParticles message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() ->
                    NaturesAura.proxy.spawnMagicParticle(Minecraft.getMinecraft().world,
                            message.posX, message.posY, message.posZ,
                            message.motionX, message.motionY, message.motionZ,
                            message.color, message.scale, message.maxAge, message.gravity, message.collision, message.fade));

            return null;
        }
    }
}