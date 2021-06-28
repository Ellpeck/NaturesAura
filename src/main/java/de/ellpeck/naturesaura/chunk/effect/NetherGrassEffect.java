package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.tuple.Pair;

public class NetherGrassEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "nether_grass");

    private int amount;
    private int dist;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(world, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura < 1500000)
            return false;
        this.amount = Math.min(20, MathHelper.ceil(Math.abs(aura) / 100000F / auraAndSpots.getRight()));
        if (this.amount <= 1)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 100000, 5, 35);
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        if (NaturesAuraAPI.instance().isEffectPowderActive(player.world, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ModBlocks.NETHER_GRASS);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (world.getGameTime() % 40 != 0)
            return;
        if (!this.calcValues(world, pos, spot))
            return;
        for (int i = this.amount / 2 + world.rand.nextInt(this.amount / 2); i >= 0; i--) {
            int x = MathHelper.floor(pos.getX() + world.rand.nextGaussian() * this.dist);
            int y = MathHelper.floor(pos.getY() + world.rand.nextGaussian() * this.dist);
            int z = MathHelper.floor(pos.getZ() + world.rand.nextGaussian() * this.dist);

            for (int yOff = -5; yOff <= 5; yOff++) {
                BlockPos goalPos = new BlockPos(x, y + yOff, z);
                if (goalPos.distanceSq(pos) <= this.dist * this.dist && world.isBlockLoaded(goalPos)) {
                    if (NaturesAuraAPI.instance().isEffectPowderActive(world, goalPos, NAME))
                        continue;
                    BlockPos up = goalPos.up();
                    if (world.getBlockState(up).isSolidSide(world, up, Direction.DOWN))
                        continue;

                    BlockState state = world.getBlockState(goalPos);
                    Block block = state.getBlock();
                    if (Tags.Blocks.NETHERRACK.contains(block)) {
                        world.setBlockState(goalPos, ModBlocks.NETHER_GRASS.getDefaultState());

                        BlockPos closestSpot = IAuraChunk.getHighestSpot(world, goalPos, 25, pos);
                        IAuraChunk.getAuraChunk(world, closestSpot).drainAura(closestSpot, 500);

                        PacketHandler.sendToAllAround(world, goalPos, 32,
                                new PacketParticles(goalPos.getX(), goalPos.getY() + 0.5F, goalPos.getZ(), PacketParticles.Type.PLANT_BOOST));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.netherGrassEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_NETHER);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
