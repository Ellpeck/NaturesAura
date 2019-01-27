package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
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
                            Multiblocks.TREE_RITUAL.forEach(pos, 'G', (dustPos, matcher) -> {
                                IBlockState state = world.getBlockState(dustPos);
                                AxisAlignedBB box = state.getBoundingBox(world, dustPos);
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        dustPos.getX() + box.minX + (box.maxX - box.minX) * world.rand.nextFloat(),
                                        dustPos.getY() + 0.1F,
                                        dustPos.getZ() + box.minZ + (box.maxZ - box.minZ) * world.rand.nextFloat(),
                                        (float) world.rand.nextGaussian() * 0.02F,
                                        world.rand.nextFloat() * 0.01F + 0.02F,
                                        (float) world.rand.nextGaussian() * 0.02F,
                                        0xf4cb42, 2F, 50, 0F, false, true);
                                return true;
                            });
                            break;
                        case 1: // Tree ritual: Consuming item
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--) {
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.5F, message.posY + 0.9F, message.posZ + 0.5F,
                                        (float) world.rand.nextGaussian() * 0.04F, world.rand.nextFloat() * 0.04F, (float) world.rand.nextGaussian() * 0.04F,
                                        0x89cc37, 1.5F, 25, 0F, false, true);
                            }
                            break;
                        case 2: // Tree ritual: Tree disappearing
                            for (int i = world.rand.nextInt(5) + 3; i >= 0; i--) {
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextFloat(), message.posY + world.rand.nextFloat(), message.posZ + world.rand.nextFloat(),
                                        0F, 0F, 0F,
                                        0x33FF33, 1F, 50, 0F, false, true);
                            }
                            break;
                        case 3: // Tree ritual: Spawn result item
                            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--) {
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX, message.posY, message.posZ,
                                        world.rand.nextGaussian() * 0.1F, world.rand.nextGaussian() * 0.1F, world.rand.nextGaussian() * 0.1F,
                                        0x89cc37, 2F, 100, 0F, true, true);
                            }
                            break;
                        case 4: // Nature altar: Conversion
                            for (int i = world.rand.nextInt(5) + 2; i >= 0; i--) {
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 0.9F + 0.25F * world.rand.nextFloat(),
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.02F, world.rand.nextFloat() * 0.02F, world.rand.nextGaussian() * 0.02F,
                                        0x00FF00, world.rand.nextFloat() * 1.5F + 0.75F, 20, 0F, false, true);
                            }
                            break;
                        case 5: // Potion generator
                            int color = message.data[0];
                            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--) {
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextFloat(),
                                        message.posY + 1.1F,
                                        message.posZ + world.rand.nextFloat(),
                                        world.rand.nextGaussian() * 0.01F, world.rand.nextFloat() * 0.1F, world.rand.nextGaussian() * 0.01F,
                                        color, 2F + world.rand.nextFloat(), 40, 0F, true, true);

                                for (int x = -1; x <= 1; x += 2)
                                    for (int z = -1; z <= 1; z += 2) {
                                        NaturesAuraAPI.instance().spawnMagicParticle(
                                                message.posX + x * 3 + 0.5F,
                                                message.posY + 2.5,
                                                message.posZ + z * 3 + 0.5F,
                                                world.rand.nextGaussian() * 0.02F,
                                                world.rand.nextFloat() * 0.04F,
                                                world.rand.nextGaussian() * 0.02F,
                                                0xd6340c, 1F + world.rand.nextFloat() * 2F, 75, 0F, true, true);
                                    }
                            }
                            break;
                        case 6: // Plant boost effect
                            for (int i = world.rand.nextInt(20) + 15; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextFloat(),
                                        message.posY + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posZ + world.rand.nextFloat(),
                                        0F, world.rand.nextFloat() * 0.02F, 0F,
                                        0x5ccc30, 1F + world.rand.nextFloat() * 2F, 50, 0F, false, true);

                            break;
                        case 7: // Flower generator consumation
                            color = message.data[0];
                            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.02F,
                                        world.rand.nextGaussian() * 0.02F,
                                        world.rand.nextGaussian() * 0.02F,
                                        color, world.rand.nextFloat() * 2F + 1F, 25, 0F, false, true);
                            break;
                        case 8: // Flower generator aura creation
                            for (int i = world.rand.nextInt(10) + 5; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 1.01F,
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.04F + 0.02F,
                                        world.rand.nextGaussian() * 0.01F,
                                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, false, true);
                            break;
                        case 9: // Placer placing
                            for (int i = world.rand.nextInt(20) + 20; i >= 0; i--) {
                                boolean side = world.rand.nextBoolean();
                                float x = side ? world.rand.nextFloat() : (world.rand.nextBoolean() ? 1.1F : -0.1F);
                                float z = !side ? world.rand.nextFloat() : (world.rand.nextBoolean() ? 1.1F : -0.1F);
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + x, message.posY + 0.1F + world.rand.nextFloat() * 0.98F, message.posZ + z,
                                        0F, 0F, 0F,
                                        0xad7a37, world.rand.nextFloat() + 1F, 50, 0F, true, true);
                            }
                            break;
                        case 10: // Hopper upgrade picking up
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX, message.posY + 0.45F, message.posZ,
                                        world.rand.nextGaussian() * 0.015F,
                                        world.rand.nextGaussian() * 0.015F,
                                        world.rand.nextGaussian() * 0.015F,
                                        0xdde7ff, world.rand.nextFloat() + 1F, 30, -0.06F, true, true);
                            break;
                        case 11: // Shockwave creator particles
                            for (int i = 0; i < 360; i += 2) {
                                double rad = Math.toRadians(i);
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX, message.posY + 0.01F, message.posZ,
                                        (float) Math.sin(rad) * 0.65F,
                                        0F,
                                        (float) Math.cos(rad) * 0.65F,
                                        0x911b07, 3F, 10, 0F, false, true);
                            }
                            break;
                        case 12: // Oak generator
                            int sapX = message.data[0];
                            int sapY = message.data[1];
                            int sapZ = message.data[2];
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnParticleStream(
                                        sapX + 0.5F + (float) world.rand.nextGaussian() * 3F,
                                        sapY + 0.5F + world.rand.nextFloat() * 4F,
                                        sapZ + 0.5F + (float) world.rand.nextGaussian() * 3F,
                                        message.posX + 0.5F,
                                        message.posY + 0.5F,
                                        message.posZ + 0.5F,
                                        0.6F, BiomeColorHelper.getFoliageColorAtPos(world, new BlockPos(sapX, sapY, sapZ)), 1.5F);
                            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 1.01F,
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.03F,
                                        world.rand.nextFloat() * 0.04F + 0.04F,
                                        world.rand.nextGaussian() * 0.03F,
                                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 60, 0F, false, true);
                            break;
                        case 13: // Offering table
                            int genX = message.data[0];
                            int genY = message.data[1];
                            int genZ = message.data[2];
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX, message.posY + 0.5F, message.posZ,
                                        world.rand.nextGaussian() * 0.02F,
                                        world.rand.nextFloat() * 0.25F,
                                        world.rand.nextGaussian() * 0.02F,
                                        0xffadfd, 1.5F, 40, 0F, false, true);
                            for (int i = world.rand.nextInt(50) + 30; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        genX + 0.5F + world.rand.nextGaussian() * 2.5F,
                                        genY + 0.1F,
                                        genZ + 0.5F + world.rand.nextGaussian() * 2.5F,
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.01F,
                                        world.rand.nextGaussian() * 0.01F,
                                        0xd3e4ff, 1.5F, 150, 0F, false, true);
                            break;
                        case 14: // Pickup stopper
                            NaturesAuraAPI.instance().spawnMagicParticle(
                                    message.posX, message.posY + 0.4F, message.posZ,
                                    world.rand.nextGaussian() * 0.005F,
                                    world.rand.nextFloat() * 0.005F,
                                    world.rand.nextGaussian() * 0.005F,
                                    0xcc3116, 1.5F, 40, 0F, false, true);
                            break;
                        case 15: // Spawn lamp
                            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.3F + world.rand.nextFloat() * 0.4F,
                                        message.posY + 0.15F + world.rand.nextFloat() * 0.5F,
                                        message.posZ + 0.3F + world.rand.nextFloat() * 0.4F,
                                        0F, 0F, 0F,
                                        0xf4a142, 1F, 30, 0F, false, true);
                            break;
                        case 16: // Animal generator aura creation
                            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                                        message.posY + 1.01F,
                                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.04F + 0.02F,
                                        world.rand.nextGaussian() * 0.01F,
                                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, false, true);
                            break;
                        case 17: // Animal generator consuming
                            boolean child = message.data[0] > 0;
                            float height = message.data[1] / 10F;
                            genX = message.data[2];
                            genY = message.data[3];
                            genZ = message.data[4];
                            for (int i = (child ? world.rand.nextInt(10) + 10 : world.rand.nextInt(20) + 20); i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextGaussian() * 0.25F,
                                        message.posY + height * 0.75F + world.rand.nextGaussian() * 0.25F,
                                        message.posZ + world.rand.nextGaussian() * 0.25F,
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.01F,
                                        world.rand.nextGaussian() * 0.01F,
                                        0x42f4c8, world.rand.nextFloat() * (child ? 0.5F : 2F) + 1F, world.rand.nextInt(30) + 40, 0F, true, true);
                            NaturesAuraAPI.instance().spawnParticleStream(
                                    message.posX, message.posY + height * 0.75F, message.posZ,
                                    genX + 0.5F, genY + 0.5F, genZ + 0.5F,
                                    0.15F, 0x41c4f4, child ? 1.5F : 3F);
                            break;
                        case 18: // End flower decay
                            color = message.data[0];
                            for (int i = world.rand.nextInt(10) + 20; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextFloat(),
                                        message.posY + world.rand.nextFloat(),
                                        message.posZ + world.rand.nextFloat(),
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.01F,
                                        world.rand.nextGaussian() * 0.01F,
                                        color, 1.5F, 80, 0F, true, true);
                            break;
                        case 19: // Animal spawner, auto crafter
                            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX, message.posY + 0.5F, message.posZ,
                                        world.rand.nextGaussian() * 0.02F,
                                        world.rand.nextFloat() * 0.02F,
                                        world.rand.nextGaussian() * 0.02F,
                                        0x16b7b2, 1.5F, 40, 0F, false, true);
                            break;
                        case 20: // RF converter
                            for (int i = world.rand.nextInt(5) + 2; i >= 0; i--)
                                Multiblocks.RF_CONVERTER.forEach(new BlockPos(message.posX, message.posY, message.posZ), 'R', (blockPos, matcher) -> {
                                    if (world.rand.nextFloat() < 0.35F) {
                                        NaturesAuraAPI.instance().spawnParticleStream(
                                                blockPos.getX() + world.rand.nextFloat(),
                                                blockPos.getY() + world.rand.nextFloat(),
                                                blockPos.getZ() + world.rand.nextFloat(),
                                                message.posX + world.rand.nextFloat(),
                                                message.posY + world.rand.nextFloat(),
                                                message.posZ + world.rand.nextFloat(),
                                                0.05F, 0xff1a05, 1.5F);
                                    }
                                    return true;
                                });
                            break;
                        case 21: // Nice looking effect
                            int excess = message.data[0];
                            double setting = ModConfig.client.excessParticleAmount;
                            if (setting >= 1 || world.rand.nextFloat() <= setting)
                                NaturesAuraAPI.instance().spawnMagicParticle(
                                        message.posX + world.rand.nextFloat(),
                                        message.posY + world.rand.nextFloat(),
                                        message.posZ + world.rand.nextFloat(),
                                        world.rand.nextGaussian() * 0.01F,
                                        world.rand.nextFloat() * 0.025F,
                                        world.rand.nextGaussian() * 0.01F,
                                        BiomeColorHelper.getFoliageColorAtPos(world, new BlockPos(message.posX, message.posY, message.posZ)),
                                        Math.min(2F, 0.5F + world.rand.nextFloat() * (excess / 1000F)),
                                        Math.min(300, 100 + world.rand.nextInt(excess / 30 + 1)),
                                        0F, false, true);
                            break;
                    }
                }
            });

            return null;
        }
    }
}