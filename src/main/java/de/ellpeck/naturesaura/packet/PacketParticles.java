package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.entities.EntityStructureFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.NetworkEvent;

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

    public static PacketParticles fromBytes(FriendlyByteBuf buf) {
        var packet = new PacketParticles();

        packet.posX = buf.readFloat();
        packet.posY = buf.readFloat();
        packet.posZ = buf.readFloat();
        packet.type = Type.values()[buf.readByte()];

        packet.data = new int[buf.readByte()];
        for (var i = 0; i < packet.data.length; i++) {
            packet.data[i] = buf.readInt();
        }

        return packet;
    }

    public static void toBytes(PacketParticles packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.posX);
        buf.writeFloat(packet.posY);
        buf.writeFloat(packet.posZ);
        buf.writeByte(packet.type.ordinal());

        buf.writeByte(packet.data.length);
        for (var i : packet.data) {
            buf.writeInt(i);
        }
    }

    // lambda causes classloading issues on a server here
    @SuppressWarnings("Convert2Lambda")
    public static void onMessage(PacketParticles message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(new Runnable() {
            @Override
            public void run() {
                Level level = Minecraft.getInstance().level;
                if (level != null)
                    message.type.action.accept(message, level);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        TR_GOLD_POWDER((message, level) -> {
            var pos = BlockPos.containing(message.posX, message.posY, message.posZ);
            Multiblocks.TREE_RITUAL.forEach(pos, 'G', (dustPos, matcher) -> {
                var state = level.getBlockState(dustPos);
                var box = state.getShape(level, dustPos).bounds();
                NaturesAuraAPI.instance().spawnMagicParticle(
                        dustPos.getX() + box.minX + (box.maxX - box.minX) * level.random.nextFloat(),
                        dustPos.getY() + 0.1F,
                        dustPos.getZ() + box.minZ + (box.maxZ - box.minZ) * level.random.nextFloat(),
                        (float) level.random.nextGaussian() * 0.02F,
                        level.random.nextFloat() * 0.01F + 0.02F,
                        (float) level.random.nextGaussian() * 0.02F,
                        0xf4cb42, 2F, 50, 0F, false, true);
                return true;
            });
        }),
        TR_CONSUME_ITEM((message, level) -> {
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.5F, message.posY + 0.9F, message.posZ + 0.5F,
                        (float) level.random.nextGaussian() * 0.04F, level.random.nextFloat() * 0.04F, (float) level.random.nextGaussian() * 0.04F,
                        0x89cc37, 1.5F, 25, 0F, false, true);
            }
        }),
        TR_DISAPPEAR((message, level) -> {
            for (var i = level.random.nextInt(5) + 3; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat(), message.posY + level.random.nextFloat(), message.posZ + level.random.nextFloat(),
                        0F, 0F, 0F,
                        0x33FF33, 1F, 50, 0F, false, true);
            }
        }),
        TR_SPAWN_RESULT((message, level) -> {
            for (var i = level.random.nextInt(10) + 10; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY, message.posZ,
                        level.random.nextGaussian() * 0.1F, level.random.nextGaussian() * 0.1F, level.random.nextGaussian() * 0.1F,
                        0x89cc37, 2F, 100, 0F, true, true);
            }
        }),
        ALTAR_CONVERSION((message, level) -> {
            var color = message.data[0];
            for (var i = level.random.nextInt(5) + 2; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 0.9F + 0.25F * level.random.nextFloat(),
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.02F, level.random.nextFloat() * 0.02F, level.random.nextGaussian() * 0.02F,
                        color, level.random.nextFloat() * 1.5F + 0.75F, 20, 0F, false, true);
            }
        }),
        POTION_GEN((message, level) -> {
            var color = message.data[0];
            var releaseAura = message.data[1] > 0;
            for (var i = level.random.nextInt(5) + 5; i >= 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat(),
                        message.posY + 1.1F,
                        message.posZ + level.random.nextFloat(),
                        level.random.nextGaussian() * 0.01F, level.random.nextFloat() * 0.1F, level.random.nextGaussian() * 0.01F,
                        color, 2F + level.random.nextFloat(), 40, 0F, true, true);

                if (releaseAura)
                    for (var x = -1; x <= 1; x += 2)
                        for (var z = -1; z <= 1; z += 2) {
                            NaturesAuraAPI.instance().spawnMagicParticle(
                                    message.posX + x * 3 + 0.5F,
                                    message.posY + 2.5,
                                    message.posZ + z * 3 + 0.5F,
                                    level.random.nextGaussian() * 0.02F,
                                    level.random.nextFloat() * 0.04F,
                                    level.random.nextGaussian() * 0.02F,
                                    0xd6340c, 1F + level.random.nextFloat() * 2F, 75, 0F, true, true);
                        }
            }
        }),
        PLANT_BOOST((message, level) -> {
            for (var i = level.random.nextInt(20) + 15; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat(),
                        message.posY + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posZ + level.random.nextFloat(),
                        0F, level.random.nextFloat() * 0.02F, 0F,
                        0x5ccc30, 1F + level.random.nextFloat() * 2F, 50, 0F, false, true);
        }),
        FLOWER_GEN_CONSUME((message, level) -> {
            var color = message.data[0];
            for (var i = level.random.nextInt(10) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.02F,
                        level.random.nextGaussian() * 0.02F,
                        level.random.nextGaussian() * 0.02F,
                        color, level.random.nextFloat() * 2F + 1F, 25, 0F, false, true);
        }),
        FLOWER_GEN_AURA_CREATION((message, level) -> {
            for (var i = level.random.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + level.random.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        PLACER_PLACING((message, level) -> {
            for (var i = level.random.nextInt(20) + 20; i >= 0; i--) {
                var side = level.random.nextBoolean();
                var x = side ? level.random.nextFloat() : level.random.nextBoolean() ? 1.1F : -0.1F;
                var z = !side ? level.random.nextFloat() : level.random.nextBoolean() ? 1.1F : -0.1F;
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + x, message.posY + 0.1F + level.random.nextFloat() * 0.98F, message.posZ + z,
                        0F, 0F, 0F,
                        0xad7a37, level.random.nextFloat() + 1F, 50, 0F, true, true);
            }
        }),
        HOPPER_UPGRADE((message, level) -> {
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.45F, message.posZ,
                        level.random.nextGaussian() * 0.015F,
                        level.random.nextGaussian() * 0.015F,
                        level.random.nextGaussian() * 0.015F,
                        0xdde7ff, level.random.nextFloat() + 1F, 30, -0.06F, true, true);
        }),
        SHOCKWAVE_CREATOR((message, level) -> {
            for (var i = 0; i < 360; i += 2) {
                var rad = Math.toRadians(i);
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.01F, message.posZ,
                        (float) Math.sin(rad) * 0.65F,
                        0F,
                        (float) Math.cos(rad) * 0.65F,
                        0x911b07, 3F, 10, 0F, false, true);
            }
        }),
        OAK_GENERATOR((message, level) -> {
            var sapX = message.data[0];
            var sapY = message.data[1];
            var sapZ = message.data[2];
            var releaseAura = message.data[3] > 0;
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        sapX + 0.5F + (float) level.random.nextGaussian() * 3F,
                        sapY + 0.5F + level.random.nextFloat() * 4F,
                        sapZ + 0.5F + (float) level.random.nextGaussian() * 3F,
                        message.posX + 0.5F,
                        message.posY + 0.5F,
                        message.posZ + 0.5F,
                        0.6F, BiomeColors.getAverageGrassColor(level, new BlockPos(sapX, sapY, sapZ)), 1.5F);
            if (releaseAura)
                for (var i = level.random.nextInt(10) + 10; i >= 0; i--)
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                            message.posY + 1.01F,
                            message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                            level.random.nextGaussian() * 0.03F,
                            level.random.nextFloat() * 0.04F + 0.04F,
                            level.random.nextGaussian() * 0.03F,
                            0x5ccc30, 1F + level.random.nextFloat() * 1.5F, 60, 0F, false, true);
        }),
        OFFERING_TABLE((message, level) -> {
            var genX = message.data[0];
            var genY = message.data[1];
            var genZ = message.data[2];
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.5F, message.posZ,
                        level.random.nextGaussian() * 0.02F,
                        level.random.nextFloat() * 0.25F,
                        level.random.nextGaussian() * 0.02F,
                        0xffadfd, 1.5F, 40, 0F, false, true);
            for (var i = level.random.nextInt(50) + 30; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        genX + 0.5F + level.random.nextGaussian() * 2.5F,
                        genY + 0.1F,
                        genZ + 0.5F + level.random.nextGaussian() * 2.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.01F,
                        level.random.nextGaussian() * 0.01F,
                        0xd3e4ff, 1.5F, 150, 0F, false, true);
        }),
        PICKUP_STOPPER((message, level) -> NaturesAuraAPI.instance().spawnMagicParticle(
                message.posX, message.posY + 0.4F, message.posZ,
                level.random.nextGaussian() * 0.005F,
                level.random.nextFloat() * 0.005F,
                level.random.nextGaussian() * 0.005F,
                0xcc3116, 1.5F, 40, 0F, false, true)),
        SPAWN_LAMP((message, level) -> {
            for (var i = level.random.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.3F + level.random.nextFloat() * 0.4F,
                        message.posY + 0.15F + level.random.nextFloat() * 0.5F,
                        message.posZ + 0.3F + level.random.nextFloat() * 0.4F,
                        0F, 0F, 0F,
                        0xf4a142, 1F, 30, 0F, false, true);
        }),
        ANIMAL_GEN_CREATE((message, level) -> {
            for (var i = level.random.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        0xd13308, 1F + level.random.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        ANIMAL_GEN_CONSUME((message, level) -> {
            var child = message.data[0] > 0;
            var height = message.data[1] / 10F;
            var genX = message.data[2];
            var genY = message.data[3];
            var genZ = message.data[4];
            for (var i = child ? level.random.nextInt(10) + 10 : level.random.nextInt(20) + 20; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextGaussian() * 0.25F,
                        message.posY + height * 0.75F + level.random.nextGaussian() * 0.25F,
                        message.posZ + level.random.nextGaussian() * 0.25F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.01F,
                        level.random.nextGaussian() * 0.01F,
                        0x42f4c8, level.random.nextFloat() * (child ? 0.5F : 2F) + 1F, level.random.nextInt(30) + 40, 0F, true, true);
            NaturesAuraAPI.instance().spawnParticleStream(
                    message.posX, message.posY + height * 0.75F, message.posZ,
                    genX + 0.5F, genY + 0.5F, genZ + 0.5F,
                    0.15F, 0x41c4f4, child ? 1.5F : 3F);
        }),
        END_FLOWER_DECAY((message, level) -> {
            var color = message.data[0];
            for (var i = level.random.nextInt(10) + 20; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat(),
                        message.posY + level.random.nextFloat(),
                        message.posZ + level.random.nextFloat(),
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.01F,
                        level.random.nextGaussian() * 0.01F,
                        color, 1.5F, 80, 0F, true, true);
        }),
        ANIMAL_SPAWNER((message, level) -> {
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.5F, message.posZ,
                        level.random.nextGaussian() * 0.02F,
                        level.random.nextFloat() * 0.02F,
                        level.random.nextGaussian() * 0.02F,
                        0x16b7b2, 1.5F, 40, 0F, false, true);
        }),
        RF_CONVERTER((message, level) -> {
            for (var i = level.random.nextInt(5) + 2; i >= 0; i--)
                Multiblocks.RF_CONVERTER.forEach(BlockPos.containing(message.posX, message.posY, message.posZ), 'R', (blockPos, matcher) -> {
                    if (level.random.nextFloat() < 0.35F) {
                        NaturesAuraAPI.instance().spawnParticleStream(
                                blockPos.getX() + level.random.nextFloat(),
                                blockPos.getY() + level.random.nextFloat(),
                                blockPos.getZ() + level.random.nextFloat(),
                                message.posX + level.random.nextFloat(),
                                message.posY + level.random.nextFloat(),
                                message.posZ + level.random.nextFloat(),
                                0.05F, 0xff1a05, 1.5F);
                    }
                    return true;
                });
        }),
        END_FLOWER_CONSUME((message, level) -> {
            var color = message.data[0];
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX, message.posY + 0.5F, message.posZ,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.01F,
                        level.random.nextGaussian() * 0.01F,
                        color, 1.5F, 40, 0F, false, true);
        }),
        MOVER_CART((message, level) -> {
            var motionX = message.data[0] / 100F;
            var motionY = message.data[1] / 100F;
            var motionZ = message.data[2] / 100F;
            for (var i = level.random.nextInt(60) + 30; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextGaussian() * 10F,
                        message.posY + level.random.nextGaussian() * 10F,
                        message.posZ + level.random.nextGaussian() * 10F,
                        motionX * 0.2F, motionY * 0.2F, motionZ * 0.2F,
                        IAuraType.forLevel(level).getColor(), 2F, 30, 0F, false, true);
        }),
        MOSS_GENERATOR((message, level) -> {
            for (var i = level.random.nextInt(30) + 30; i >= 0; i--) {
                var side = level.random.nextInt(3);
                var x = side != 0 ? level.random.nextFloat() : level.random.nextBoolean() ? 1.1F : -0.1F;
                var y = side != 1 ? level.random.nextFloat() : level.random.nextBoolean() ? 1.1F : -0.1F;
                var z = side != 2 ? level.random.nextFloat() : level.random.nextBoolean() ? 1.1F : -0.1F;
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + x,
                        message.posY + y,
                        message.posZ + z,
                        0F, 0F, 0F,
                        0x184c0d, level.random.nextFloat() + 1F, 30, 0F, true, true);
            }
            for (var i = level.random.nextInt(20) + 10; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat(),
                        message.posY + 1F,
                        message.posZ + level.random.nextFloat(),
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + level.random.nextFloat() * 1.5F, 40, 0F, true, true);
        }),
        FIREWORK_GEN((message, level) -> {
            var goalX = message.data[0];
            var goalY = message.data[1];
            var goalZ = message.data[2];
            NaturesAuraAPI.instance().setParticleSpawnRange(64);
            for (var i = level.random.nextInt(30) + 30; i >= 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        message.posX + (float) level.random.nextGaussian(),
                        message.posY + (float) level.random.nextGaussian(),
                        message.posZ + (float) level.random.nextGaussian(),
                        goalX + 0.25F + level.random.nextFloat() * 0.5F,
                        goalY + 0.25F + level.random.nextFloat() * 0.5F,
                        goalZ + 0.25F + level.random.nextFloat() * 0.5F,
                        0.65F, message.data[3 + level.random.nextInt(message.data.length - 3)], 1F);
            NaturesAuraAPI.instance().setParticleSpawnRange(32);
        }),
        DIMENSION_RAIL((message, level) -> {
            var width = message.data[0] / 100F;
            var height = message.data[1] / 100F;
            var depth = message.data[2] / 100F;
            for (var i = level.random.nextInt(100) + 50; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextFloat() * width,
                        message.posY + level.random.nextFloat() * height,
                        message.posZ + level.random.nextFloat() * depth,
                        0F, 0F, 0F, 0xd60cff, 1F + level.random.nextFloat(), 60, 0F, false, true);
        }),
        PROJECTILE_GEN((message, level) -> {
            var x = message.data[0];
            var y = message.data[1];
            var z = message.data[2];
            for (var i = level.random.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        x + 0.25F + level.random.nextFloat() * 0.5F,
                        y + 1.01F,
                        z + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        0x5ccc30, 1F + level.random.nextFloat() * 1.5F, 40, 0F, false, true);
            for (var i = level.random.nextInt(10) + 10; i >= 0; i--)
                level.addParticle(ParticleTypes.FIREWORK,
                        message.posX, message.posY, message.posZ,
                        level.random.nextGaussian() * 0.03F,
                        level.random.nextGaussian() * 0.03F,
                        level.random.nextGaussian() * 0.03F);
        }),
        BLAST_FURNACE_BOOSTER((message, level) -> {
            var worked = message.data[0] > 0;
            for (var i = level.random.nextInt(10) + 5; i >= 0; i--)
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        message.posX + 5 / 16F + level.random.nextInt(6) / 16F,
                        message.posY + 0.6F,
                        message.posZ + 5 / 16F + level.random.nextInt(6) / 16F,
                        level.random.nextGaussian() * 0.005F,
                        level.random.nextFloat() * 0.02F + 0.01F,
                        level.random.nextGaussian() * 0.005F);

            if (worked) {
                var pos = BlockPos.containing(message.posX, message.posY, message.posZ);
                var color = IAuraChunk.getAuraChunk(level, pos).getType().getColor();
                for (var i = level.random.nextInt(10) + 10; i >= 0; i--)
                    NaturesAuraAPI.instance().spawnParticleStream(
                            message.posX + (float) level.random.nextGaussian() * 5,
                            message.posY + level.random.nextFloat() * 5,
                            message.posZ + (float) level.random.nextGaussian() * 5,
                            message.posX + 0.5F, message.posY + 0.5F, message.posZ + 0.5F,
                            0.25F, color, 0.5F + level.random.nextFloat()
                    );
            }
        }),
        ANIMAL_CONTAINER((message, level) -> {
            for (var i = level.random.nextInt(2) + 1; i > 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + level.random.nextGaussian() * 0.15F,
                        message.posY - level.random.nextFloat() * 0.5F,
                        message.posZ + level.random.nextGaussian() * 0.15F,
                        0, 0, 0, 0x42e9f5, 1 + level.random.nextFloat() * 2, 40, 0, false, true
                );
        }),
        SNOW_CREATOR((message, level) -> {
            var pos = BlockPos.containing(message.posX, message.posY, message.posZ);
            var color = IAuraChunk.getAuraChunk(level, pos).getType().getColor();
            for (var i = level.random.nextInt(3) + 1; i > 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        message.posX + (float) level.random.nextGaussian() * 5,
                        message.posY + level.random.nextFloat() * 5,
                        message.posZ + (float) level.random.nextGaussian() * 5,
                        message.posX + 0.5F, message.posY + 0.5F, message.posZ + 0.5F,
                        0.25F, color, 0.5F + level.random.nextFloat()
                );
        }),
        CHORUS_GENERATOR((message, level) -> {
            var chorusX = message.data[0];
            var chorusY = message.data[1];
            var chorusZ = message.data[2];
            for (var i = level.random.nextInt(5) + 3; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        chorusX + level.random.nextFloat(), chorusY + level.random.nextFloat(), chorusZ + level.random.nextFloat(),
                        0F, 0F, 0F,
                        0xbb0be3, 1F + level.random.nextFloat(), 50, 0F, false, true);
            for (var i = level.random.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        IAuraType.forLevel(level).getColor(), 1F + level.random.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        TIMER_RESET((message, level) -> {
            var color = message.data[0];
            for (var i = level.random.nextInt(10) + 15; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 5 / 16F + level.random.nextFloat() * 6 / 16F,
                        message.posY + 2 / 16F + level.random.nextFloat() * 8 / 16F,
                        message.posZ + 5 / 16F + level.random.nextFloat() * 6 / 16F,
                        0, 0, 0, color, 2, 40 + level.random.nextInt(20), 0, false, true);
        }),
        STRUCTURE_FINDER((message, level) -> {
            var entity = (EntityStructureFinder) level.getEntity(message.data[0]);
            var renderer = Minecraft.getInstance().levelRenderer;

            var d0 = message.posX + 0.5D;
            double d13 = message.posY;
            var d18 = message.posZ + 0.5D;
            for (var j2 = 0; j2 < 8; ++j2)
                renderer.addParticle(new ItemParticleOption(ParticleTypes.ITEM, entity.getItem()), false, d0, d13, d18, level.random.nextGaussian() * 0.15D, level.random.nextDouble() * 0.2D, level.random.nextGaussian() * 0.15D);

            int color = entity.getEntityData().get(EntityStructureFinder.COLOR);
            for (var d24 = 0.0D; d24 < Math.PI * 2D; d24 += 0.15707963267948966D) {
                NaturesAuraAPI.instance().spawnMagicParticle(d0 + Math.cos(d24) * 5.0D, d13 - 0.4D, d18 + Math.sin(d24) * 5.0D, Math.cos(d24) * -2, 0.0D, Math.sin(d24) * -2, color, 2, 60, 0, false, true);
                NaturesAuraAPI.instance().spawnMagicParticle(d0 + Math.cos(d24) * 5.0D, d13 - 0.4D, d18 + Math.sin(d24) * 5.0D, Math.cos(d24) * -2.5, 0.0D, Math.sin(d24) * -2.5, color, 2, 60, 0, false, true);
            }
        }),
        SLIME_SPLIT_GEN_CREATE((message, level) -> {
            for (var i = level.random.nextInt(5) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + 0.25F + level.random.nextFloat() * 0.5F,
                        message.posY + 1.01F,
                        message.posZ + 0.25F + level.random.nextFloat() * 0.5F,
                        level.random.nextGaussian() * 0.01F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.01F,
                        message.data[0], 1F + level.random.nextFloat() * 1.5F, 40, 0F, false, true);
        }),
        SLIME_SPLIT_GEN_START((message, level) -> {
            var x = message.data[0];
            var y = message.data[1];
            var z = message.data[2];
            var color = message.data[3];
            for (var i = level.random.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + (float) level.random.nextGaussian() * 0.5F,
                        message.posY + (float) level.random.nextGaussian() * 0.5F,
                        message.posZ + (float) level.random.nextGaussian() * 0.5F,
                        level.random.nextGaussian() * 0.02F,
                        level.random.nextFloat() * 0.04F + 0.02F,
                        level.random.nextGaussian() * 0.02F,
                        color, level.random.nextFloat() + 1, level.random.nextInt(20) + 20, 0, false, true);
            for (var i = level.random.nextInt(10) + 5; i >= 0; i--)
                NaturesAuraAPI.instance().spawnParticleStream(
                        message.posX + (float) level.random.nextGaussian() * 0.5F,
                        message.posY + (float) level.random.nextGaussian() * 0.5F,
                        message.posZ + (float) level.random.nextGaussian() * 0.5F,
                        x + 0.5F, y + 0.5F, z + 0.5F, 0.2F, color, level.random.nextFloat() + 1);
        }),
        PET_REVIVER((message, level) -> {
            for (var i = level.random.nextInt(50) + 150; i >= 0; i--)
                NaturesAuraAPI.instance().spawnMagicParticle(
                        message.posX + (float) level.random.nextGaussian() * 0.4F,
                        message.posY + (float) level.random.nextGaussian() * 0.4F,
                        message.posZ + (float) level.random.nextGaussian() * 0.4F,
                        level.random.nextGaussian() * 0.002F,
                        level.random.nextFloat() * 0.001F + 0.002F,
                        level.random.nextGaussian() * 0.002F,
                        message.data[0], level.random.nextFloat() * 2 + 1, level.random.nextInt(50) + 50, 0, false, true);
        });

        public final BiConsumer<PacketParticles, Level> action;

        Type(BiConsumer<PacketParticles, Level> action) {
            this.action = action;
        }
    }
}
