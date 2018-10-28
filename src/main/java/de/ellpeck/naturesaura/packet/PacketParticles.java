package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.Multiblocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
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
    private int[] data;

    public PacketParticles(float posX, float posY, float posZ, int type, int... data) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.type = type;
        this.data = data;
    }

    public PacketParticles() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.type = buf.readByte();

        this.data = new int[buf.readByte()];
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.posX);
        buf.writeFloat(this.posY);
        buf.writeFloat(this.posZ);
        buf.writeByte(this.type);

        buf.writeByte(this.data.length);
        for (int i : this.data) {
            buf.writeInt(i);
        }
    }

    public static class Handler implements IMessageHandler<PacketParticles, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketParticles message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world != null) {
                    switch (message.type) {
                        case 0: // Tree ritual: Gold powder
                            BlockPos pos = new BlockPos(message.posX, message.posY, message.posZ);
                            Multiblocks.TREE_RITUAL.forEach(world, pos, Rotation.NONE, 'G', dustPos -> {
                                IBlockState state = world.getBlockState(dustPos);
                                AxisAlignedBB box = state.getBoundingBox(world, dustPos);
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        dustPos.getX() + box.minX + (box.maxX - box.minX) * world.rand.nextFloat(),
                                        dustPos.getY() + 0.1F,
                                        dustPos.getZ() + box.minZ + (box.maxZ - box.minZ) * world.rand.nextFloat(),
                                        (float) world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.005F + 0.01F,
                                        (float) world.rand.nextGaussian() * 0.01F,
                                        0xf4cb42, 2F, 100, 0F, false, true);
                            });
                            break;
                        case 1: // Tree ritual: Consuming item
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + 0.5F, message.posY + 0.9F, message.posZ + 0.5F,
                                        (float) world.rand.nextGaussian() * 0.02F, world.rand.nextFloat() * 0.02F, (float) world.rand.nextGaussian() * 0.02F,
                                        0x89cc37, 1.5F, 50, 0F, false, true);
                            }
                            break;
                        case 2: // Tree ritual: Tree disappearing
                            for (int i = world.rand.nextInt(5) + 3; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + world.rand.nextFloat(), message.posY + world.rand.nextFloat(), message.posZ + world.rand.nextFloat(),
                                        0F, 0F, 0F,
                                        0x33FF33, 1F, 100, 0F, false, true);
                            }
                            break;
                        case 3: // Tree ritual: Spawn result item
                            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX, message.posY, message.posZ,
                                        world.rand.nextGaussian() * 0.05F, world.rand.nextGaussian() * 0.05F, world.rand.nextGaussian() * 0.05F,
                                        0x89cc37, 2F, 200, 0F, true, true);
                            }
                            break;
                        case 4: // Nature altar: Conversion
                            for (int i = world.rand.nextInt(5) + 2; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 0.9F + 0.25F * world.rand.nextFloat(),
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.01F, world.rand.nextFloat() * 0.01F, world.rand.nextGaussian() * 0.01F,
                                        0x00FF00, world.rand.nextFloat() * 1.5F + 0.75F, 40, 0F, false, true);
                            }
                            break;
                        case 5: // Potion generator
                            int color = message.data[0];
                            boolean disperse = message.data[1] > 0;
                            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--) {
                                NaturesAura.proxy.spawnMagicParticle(world,
                                        message.posX + world.rand.nextFloat(),
                                        message.posY + 1.1F,
                                        message.posZ + world.rand.nextFloat(),
                                        world.rand.nextGaussian() * 0.005F, world.rand.nextFloat() * 0.05F, world.rand.nextGaussian() * 0.005F,
                                        color, 2F + world.rand.nextFloat(), 80, 0F, true, true);

                                if (disperse)
                                    for (int x = -1; x <= 1; x += 2)
                                        for (int z = -1; z <= 1; z += 2) {
                                            NaturesAura.proxy.spawnMagicParticle(world,
                                                    message.posX + x * 3 + 0.5F,
                                                    message.posY + 2.5,
                                                    message.posZ + z * 3 + 0.5F,
                                                    world.rand.nextGaussian() * 0.01F,
                                                    world.rand.nextFloat() * 0.02F,
                                                    world.rand.nextGaussian() * 0.01F,
                                                    0xd6340c, 1F + world.rand.nextFloat() * 2F, 150, 0F, true, true);
                                        }
                            }
                            break;
                    }
                }
            });

            return null;
        }
    }
}