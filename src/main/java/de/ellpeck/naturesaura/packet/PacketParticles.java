package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PacketParticles {

    private float posX;
    private float posY;
    private float posZ;
    private Type type;
    private int[] data;

    public PacketParticles(float posX, float posY, float posZ, Type type, int... data) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.type = type;
        this.data = data;
    }

    private PacketParticles() {
    }

    public static PacketParticles fromBytes(PacketBuffer buf) {
        PacketParticles packet = new PacketParticles();

        packet.posX = buf.readFloat();
        packet.posY = buf.readFloat();
        packet.posZ = buf.readFloat();
        packet.type = Type.values()[buf.readByte()];

        packet.data = new int[buf.readByte()];
        for (int i = 0; i < packet.data.length; i++) {
            packet.data[i] = buf.readInt();
        }

        return packet;
    }

    public static void toBytes(PacketParticles packet, PacketBuffer buf) {
        buf.writeFloat(packet.posX);
        buf.writeFloat(packet.posY);
        buf.writeFloat(packet.posZ);
        buf.writeByte(packet.type.ordinal());

        buf.writeByte(packet.data.length);
        for (int i : packet.data) {
            buf.writeInt(i);
        }
    }

    // lambda causes classloading issues on a server here
    @SuppressWarnings("Convert2Lambda")
    public static void onMessage(PacketParticles message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(new Runnable() {
            @Override
            public void run() {
                World world = Minecraft.getInstance().world;
                if (world != null)
                    message.type.action.accept(message, world);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        TR_GOLD_POWDER((message, world) -> {
            BlockPos pos = new BlockPos(message.posX, message.posY, message.posZ);
            Multiblocks.TREE_RITUAL.forEach(pos, 'G', (dustPos, matcher) -> {
                BlockState state = world.getBlockState(dustPos);
                AxisAlignedBB box = state.getShape(world, dustPos).getBoundingBox();
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
        }),
        TR_CONSUME_ITEM((message, world) -> {
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.5F, message.posY + 0.9F, message.posZ + 0.5F,
                        (float) world.rand.nextGaussian() * 0.04F, world.rand.nextFloat() * 0.04F, (float) world.rand.nextGaussian() * 0.04F,
                        0x89cc37, 1.5F, 25, 0F, false, true);
            }
        }),
        TR_DISAPPEAR((message, world) -> {
            for (int i = world.rand.nextInt(5) + 3; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat(), message.posY + world.rand.nextFloat(), message.posZ + world.rand.nextFloat(),
                        0F, 0F, 0F,
                        0x33FF33, 1F, 50, 0F, false, true);
            }
        }),
        TR_SPAWN_RESULT((message, world) -> {
            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY, message.posZ,
                        world.rand.nextGaussian() * 0.1F, world.rand.nextGaussian() * 0.1F, world.rand.nextGaussian() * 0.1F,
                        0x89cc37, 2F, 100, 0F, true, true);
            }
        }),
        ALTAR_CONVERSION((message, world) -> {
            int color = message.data[0];
            for (int i = world.rand.nextInt(5) + 2; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posY + 0.9F + 0.25F * world.rand.nextFloat(),
                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                        world.rand.nextGaussian() * 0.02F, world.rand.nextFloat() * 0.02F, world.rand.nextGaussian() * 0.02F,
                        color, world.rand.nextFloat() * 1.5F + 0.75F, 20, 0F, false, true);
            }
        }),
        POTION_GEN((message, world) -> {
            int color = message.data[0];
            boolean releaseAura = message.data[1] > 0;
            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat(),
                        message.posY + 1.1F,
                        message.posZ + world.rand.nextFloat(),
                        world.rand.nextGaussian() * 0.01F, world.rand.nextFloat() * 0.1F, world.rand.nextGaussian() * 0.01F,
                        color, 2F + world.rand.nextFloat(), 40, 0F, true, true);

                if (releaseAura)
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
        }),
        PLANT_BOOST((message, world) -> {
            for (int i = world.rand.nextInt(20) + 15; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat(),
                        message.posY + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posZ + world.rand.nextFloat(),
                        0F, world.rand.nextFloat() * 0.02F, 0F,
                        0x5ccc30, 1F + world.rand.nextFloat() * 2F, 50, 0F, false, true);
        }),
        FLOWER_GEN_CONSUME((message, world) -> {
            int color = message.data[0];
            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posY + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                        world.rand.nextGaussian() * 0.02F,
                        world.rand.nextGaussian() * 0.02F,
                        world.rand.nextGaussian() * 0.02F,
                        color, world.rand.nextFloat() * 2F + 1F, 25, 0F, false, true);
        }),
        FLOWER_GEN_AURA_CREATION((message, world) -> {
            for (int i = world.rand.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.04F + 0.02F,
                        world.rand.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        PLACER_PLACING((message, world) -> {
            for (int i = world.rand.nextInt(20) + 20; i >= 0; i--) {
                boolean side = world.rand.nextBoolean();
                float x = side ? world.rand.nextFloat() : world.rand.nextBoolean() ? 1.1F : -0.1F;
                float z = !side ? world.rand.nextFloat() : world.rand.nextBoolean() ? 1.1F : -0.1F;
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + x, message.posY + 0.1F + world.rand.nextFloat() * 0.98F, message.posZ + z,
                        0F, 0F, 0F,
                        0xad7a37, world.rand.nextFloat() + 1F, 50, 0F, true, true);
            }
        }),
        HOPPER_UPGRADE((message, world) -> {
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.45F, message.posZ,
                        world.rand.nextGaussian() * 0.015F,
                        world.rand.nextGaussian() * 0.015F,
                        world.rand.nextGaussian() * 0.015F,
                        0xdde7ff, world.rand.nextFloat() + 1F, 30, -0.06F, true, true);
        }),
        SHOCKWAVE_CREATOR((message, world) -> {
            for (int i = 0; i < 360; i += 2) {
                double rad = Math.toRadians(i);
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.01F, message.posZ,
                        (float) Math.sin(rad) * 0.65F,
                        0F,
                        (float) Math.cos(rad) * 0.65F,
                        0x911b07, 3F, 10, 0F, false, true);
            }
        }),
        OAK_GENERATOR((message, world) -> {
            int sapX = message.data[0];
            int sapY = message.data[1];
            int sapZ = message.data[2];
            boolean releaseAura = message.data[3] > 0;
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        sapX + 0.5F + (float) world.rand.nextGaussian() * 3F,
                        sapY + 0.5F + world.rand.nextFloat() * 4F,
                        sapZ + 0.5F + (float) world.rand.nextGaussian() * 3F,
                        message.posX + 0.5F,
                        message.posY + 0.5F,
                        message.posZ + 0.5F,
                        0.6F, BiomeColors.func_228361_b_(world, new BlockPos(sapX, sapY, sapZ)), 1.5F);
            if (releaseAura)
                for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                            message.posY + 1.01F,
                            message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                            world.rand.nextGaussian() * 0.03F,
                            world.rand.nextFloat() * 0.04F + 0.04F,
                            world.rand.nextGaussian() * 0.03F,
                            0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 60, 0F, false, true);
        }),
        OFFERING_TABLE((message, world) -> {
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
        }),
        PICKUP_STOPPER((message, world) -> {
            NaturesAuraAPI.instance().spawnMagicParticle(
                    message.posX, message.posY + 0.4F, message.posZ,
                    world.rand.nextGaussian() * 0.005F,
                    world.rand.nextFloat() * 0.005F,
                    world.rand.nextGaussian() * 0.005F,
                    0xcc3116, 1.5F, 40, 0F, false, true);
        }),
        SPAWN_LAMP((message, world) -> {
            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.3F + world.rand.nextFloat() * 0.4F,
                        message.posY + 0.15F + world.rand.nextFloat() * 0.5F,
                        message.posZ + 0.3F + world.rand.nextFloat() * 0.4F,
                        0F, 0F, 0F,
                        0xf4a142, 1F, 30, 0F, false, true);
        }),
        ANIMAL_GEN_CREATE((message, world) -> {
            for (int i = world.rand.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + world.rand.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + world.rand.nextFloat() * 0.5F,
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.04F + 0.02F,
                        world.rand.nextGaussian() * 0.01F,
                        0xd13308, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        ANIMAL_GEN_CONSUME((message, world) -> {
            boolean child = message.data[0] > 0;
            float height = message.data[1] / 10F;
            int genX = message.data[2];
            int genY = message.data[3];
            int genZ = message.data[4];
            for (int i = child ? world.rand.nextInt(10) + 10 : world.rand.nextInt(20) + 20; i >= 0; i--)
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
        }),
        END_FLOWER_DECAY((message, world) -> {
            int color = message.data[0];
            for (int i = world.rand.nextInt(10) + 20; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat(),
                        message.posY + world.rand.nextFloat(),
                        message.posZ + world.rand.nextFloat(),
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.01F,
                        world.rand.nextGaussian() * 0.01F,
                        color, 1.5F, 80, 0F, true, true);
        }),
        ANIMAL_SPAWNER((message, world) -> {
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.5F, message.posZ,
                        world.rand.nextGaussian() * 0.02F,
                        world.rand.nextFloat() * 0.02F,
                        world.rand.nextGaussian() * 0.02F,
                        0x16b7b2, 1.5F, 40, 0F, false, true);
        }),
        RF_CONVERTER((message, world) -> {
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
        }),
        END_FLOWER_CONSUME((message, world) -> {
            int color = message.data[0];
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.5F, message.posZ,
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.01F,
                        world.rand.nextGaussian() * 0.01F,
                        color, 1.5F, 40, 0F, false, true);
        }),
        MOVER_CART((message, world) -> {
            float motionX = message.data[0] / 100F;
            float motionY = message.data[1] / 100F;
            float motionZ = message.data[2] / 100F;
            for (int i = world.rand.nextInt(60) + 30; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextGaussian() * 10F,
                        message.posY + world.rand.nextGaussian() * 10F,
                        message.posZ + world.rand.nextGaussian() * 10F,
                        motionX * 0.2F, motionY * 0.2F, motionZ * 0.2F,
                        IAuraType.forWorld(world).getColor(), 2F, 30, 0F, false, true);
        }),
        MOSS_GENERATOR((message, world) -> {
            for (int i = world.rand.nextInt(30) + 30; i >= 0; i--) {
                int side = world.rand.nextInt(3);
                float x = side != 0 ? world.rand.nextFloat() : world.rand.nextBoolean() ? 1.1F : -0.1F;
                float y = side != 1 ? world.rand.nextFloat() : world.rand.nextBoolean() ? 1.1F : -0.1F;
                float z = side != 2 ? world.rand.nextFloat() : world.rand.nextBoolean() ? 1.1F : -0.1F;
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + x,
                        message.posY + y,
                        message.posZ + z,
                        0F, 0F, 0F,
                        0x184c0d, world.rand.nextFloat() + 1F, 30, 0F, true, true);
            }
            for (int i = world.rand.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat(),
                        message.posY + 1F,
                        message.posZ + world.rand.nextFloat(),
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.04F + 0.02F,
                        world.rand.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, true, true);
        }),
        FIREWORK_GEN((message, world) -> {
            int goalX = message.data[0];
            int goalY = message.data[1];
            int goalZ = message.data[2];
            NaturesAuraAPI.instance().setParticleSpawnRange(64);
            for (int i = world.rand.nextInt(30) + 30; i >= 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        message.posX + (float) world.rand.nextGaussian(),
                        message.posY + (float) world.rand.nextGaussian(),
                        message.posZ + (float) world.rand.nextGaussian(),
                        goalX + 0.25F + world.rand.nextFloat() * 0.5F,
                        goalY + 0.25F + world.rand.nextFloat() * 0.5F,
                        goalZ + 0.25F + world.rand.nextFloat() * 0.5F,
                        0.65F, message.data[3 + world.rand.nextInt(message.data.length - 3)], 1F);
            NaturesAuraAPI.instance().setParticleSpawnRange(32);
        }),
        DIMENSION_RAIL((message, world) -> {
            float width = message.data[0] / 100F;
            float height = message.data[1] / 100F;
            float depth = message.data[2] / 100F;
            for (int i = world.rand.nextInt(100) + 50; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextFloat() * width,
                        message.posY + world.rand.nextFloat() * height,
                        message.posZ + world.rand.nextFloat() * depth,
                        0F, 0F, 0F, 0xd60cff, 1F + world.rand.nextFloat(), 60, 0F, false, true);
        }),
        PROJECTILE_GEN((message, world) -> {
            int x = message.data[0];
            int y = message.data[1];
            int z = message.data[2];
            for (int i = world.rand.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        x + 0.25F + world.rand.nextFloat() * 0.5F,
                        y + 1.01F,
                        z + 0.25F + world.rand.nextFloat() * 0.5F,
                        world.rand.nextGaussian() * 0.01F,
                        world.rand.nextFloat() * 0.04F + 0.02F,
                        world.rand.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 40, 0F, false, true);
            for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                world.addParticle(ParticleTypes.FIREWORK,
                        message.posX, message.posY, message.posZ,
                        world.rand.nextGaussian() * 0.03F,
                        world.rand.nextGaussian() * 0.03F,
                        world.rand.nextGaussian() * 0.03F);
        }),
        BLAST_FURNACE_BOOSTER((message, world) -> {
            boolean worked = message.data[0] > 0;
            for (int i = world.rand.nextInt(10) + 5; i >= 0; i--)
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        message.posX + 5 / 16F + world.rand.nextInt(6) / 16F,
                        message.posY + 0.6F,
                        message.posZ + 5 / 16F + world.rand.nextInt(6) / 16F,
                        world.rand.nextGaussian() * 0.005F,
                        world.rand.nextFloat() * 0.02F + 0.01F,
                        world.rand.nextGaussian() * 0.005F);

            if (worked) {
                BlockPos pos = new BlockPos(message.posX, message.posY, message.posZ);
                int color = IAuraChunk.getAuraChunk(world, pos).getType().getColor();
                for (int i = world.rand.nextInt(10) + 10; i >= 0; i--)
                    NaturesAuraAPI.instance().spawnParticleStream(
                            message.posX + (float) world.rand.nextGaussian() * 5,
                            message.posY + world.rand.nextFloat() * 5,
                            message.posZ + (float) world.rand.nextGaussian() * 5,
                            message.posX + 0.5F, message.posY + 0.5F, message.posZ + 0.5F,
                            0.25F, color, 0.5F + world.rand.nextFloat()
                    );
            }
        }),
        ANIMAL_CONTAINER((message, world) -> {
            for (int i = world.rand.nextInt(2) + 1; i > 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + world.rand.nextGaussian() * 0.15F,
                        message.posY - world.rand.nextFloat() * 0.5F,
                        message.posZ + world.rand.nextGaussian() * 0.15F,
                        0, 0, 0, 0x42e9f5, 1 + world.rand.nextFloat() * 2, 40, 0, false, true
                );
        }),
        SNOW_CREATOR((message, world) -> {
            BlockPos pos = new BlockPos(message.posX, message.posY, message.posZ);
            int color = IAuraChunk.getAuraChunk(world, pos).getType().getColor();
            for (int i = world.rand.nextInt(3) + 1; i > 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        message.posX + (float) world.rand.nextGaussian() * 5,
                        message.posY + world.rand.nextFloat() * 5,
                        message.posZ + (float) world.rand.nextGaussian() * 5,
                        message.posX + 0.5F, message.posY + 0.5F, message.posZ + 0.5F,
                        0.25F, color, 0.5F + world.rand.nextFloat()
                );
        });

        public final BiConsumer<PacketParticles, World> action;

        Type(BiConsumer<PacketParticles, World> action) {
            this.action = action;
        }
    }
}
