package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Pair;

public class NetherDecayEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "nether_decay");

    private int amount;
    private int dist;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot >= 0)
            return false;
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(world, pos, 50);
        int aura = auraAndSpots.getLeft();
        if (aura >= 0)
            return false;
        this.amount = Math.min(300, MathHelper.ceil(Math.abs(aura) / 50000F / auraAndSpots.getRight()));
        if (this.amount <= 1)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 50000, 5, 75);
        return true;
    }

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return ActiveType.INACTIVE;
        if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > this.dist * this.dist)
            return ActiveType.INACTIVE;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Items.SOUL_SAND);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(world, pos, spot))
            return;
        for (int i = this.amount / 2 + world.rand.nextInt(this.amount / 2); i >= 0; i--) {
            BlockPos offset = new BlockPos(
                    pos.getX() + world.rand.nextGaussian() * this.dist,
                    pos.getY() + world.rand.nextGaussian() * this.dist,
                    pos.getZ() + world.rand.nextGaussian() * this.dist);
            if (offset.distanceSq(pos) > this.dist * this.dist || !world.isBlockLoaded(offset))
                continue;

            // degrade blocks
            Block degraded = null;
            BlockState state = world.getBlockState(offset);
            if (state.getBlock() == Blocks.GLOWSTONE) {
                degraded = Blocks.NETHERRACK;
            } else if (state.getBlock().isIn(BlockTags.NYLIUM) || state.getBlock() == Blocks.NETHERRACK) {
                degraded = Blocks.SOUL_SOIL;
            } else if (state.getBlock() == Blocks.SOUL_SOIL) {
                degraded = Blocks.SOUL_SAND;
            }
            if (degraded != null) {
                world.playEvent(2001, offset, Block.getStateId(state));
                world.setBlockState(offset, degraded.getDefaultState());
            }

            // ignite blocks
            if (AbstractFireBlock.canLightBlock(world, offset, Direction.NORTH)) {
                BlockState fire = AbstractFireBlock.getFireForPlacement(world, offset);
                world.setBlockState(offset, fire);
                world.playEvent(1009, offset, 0);
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.instance.netherDecayEffect.get() && type.isSimilar(NaturesAuraAPI.TYPE_NETHER);
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
