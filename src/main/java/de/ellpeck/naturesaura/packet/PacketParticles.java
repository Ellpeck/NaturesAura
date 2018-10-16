package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticles implements IMessage {

    private float posX;
    private float posY;
    private float posZ;
    private int type;

    public PacketParticles(float posX, float posY, float posZ, int type) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.type = type;
    }

    public PacketParticles() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.type = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.posX);
        buf.writeFloat(this.posY);
        buf.writeFloat(this.posZ);
        buf.writeInt(this.type);
    }

    public static class Handler implements IMessageHandler<PacketParticles, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketParticles message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world != null) {
                    switch (message.type) {
                        case 0:
                            BlockPos pos = new BlockPos(message.posX, message.posY, message.posZ);
                            for (BlockPos offset : TileEntityWoodStand.GOLD_POWDER_POSITIONS) {
                                BlockPos dustPos = pos.add(offset);
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        dustPos.getX() + 0.375F + world.rand.nextFloat() * 0.25F,
                                        dustPos.getY() + 0.1F,
                                        dustPos.getZ() + 0.375F + world.rand.nextFloat() * 0.25F,
                                        (float) world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.005F + 0.01F,
                                        (float) world.rand.nextGaussian() * 0.01F,
                                        0xf4cb42, 2F, 100, 0F, false, true);
                            }
                            break;
                        case 1:
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + 0.5F, message.posY + 1.25F, message.posZ + 0.5F,
                                        (float) world.rand.nextGaussian() * 0.05F, world.rand.nextFloat() * 0.05F, (float) world.rand.nextGaussian() * 0.05F,
                                        0xFF00FF, 1.5F, 50, 0F, false, true);
                            }
                            break;
                        case 2:
                            for (int i = world.rand.nextInt(5) + 3; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + world.rand.nextFloat(), message.posY + world.rand.nextFloat(), message.posZ + world.rand.nextFloat(),
                                        0F, 0F, 0F,
                                        0x33FF33, 1F, 100, 0F, false, true);
                            }
                            break;
                        case 3:
                            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX, message.posY, message.posZ,
                                        world.rand.nextGaussian() * 0.05F, world.rand.nextGaussian() * 0.05F, world.rand.nextGaussian() * 0.05F,
                                        0xFF00FF, 2F, 200, 0F, true, true);
                            }
                            break;
                    }
                }
            });

            return null;
        }
    }
}