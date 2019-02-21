package de.ellpeck.naturesaura.chunk.effect;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.recipes.WeightedOre;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class OreSpawnEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesAura.MOD_ID, "ore_spawn");

    private int amount;
    private int dist;

    private boolean calcValues(World world, BlockPos pos, Integer spot) {
        if (spot <= 0)
            return false;
        int aura = IAuraChunk.getAuraInArea(world, pos, 30);
        if (aura <= 2000000)
            return false;
        this.amount = Math.min(20, MathHelper.ceil(Math.abs(aura) / 300000F / IAuraChunk.getSpotAmountInArea(world, pos, 30)));
        if (this.amount <= 0)
            return false;
        this.dist = MathHelper.clamp(Math.abs(aura) / 150000, 5, 20);
        return true;
    }

    @Override
    public int isActiveHere(EntityPlayer player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (!this.calcValues(player.world, pos, spot))
            return -1;
        if (player.getDistanceSq(pos) > this.dist * this.dist)
            return -1;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.world, player.getPosition(), NAME))
            return 0;
        return 1;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(Blocks.DIAMOND_ORE);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (world.getTotalWorldTime() % 40 != 0)
            return;
        if (!this.calcValues(world, pos, spot))
            return;
        IAuraType type = auraChunk.getType();
        Block requiredBlock;
        List<WeightedOre> ores;
        if (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)) {
            requiredBlock = Blocks.STONE;
            ores = NaturesAuraAPI.OVERWORLD_ORES;
        } else {
            requiredBlock = Blocks.NETHERRACK;
            ores = NaturesAuraAPI.NETHER_ORES;
        }
        int totalWeight = WeightedRandom.getTotalWeight(ores);

        List<Tuple<Vec3d, Integer>> powders = NaturesAuraAPI.instance().getActiveEffectPowders(world,
                new AxisAlignedBB(pos).grow(this.dist), NAME);
        if (powders.isEmpty())
            return;
        for (int i = 0; i < this.amount; i++) {
            Tuple<Vec3d, Integer> powder = powders.get(i % powders.size());
            Vec3d powderPos = powder.getFirst();
            int range = powder.getSecond();
            int x = MathHelper.floor(powderPos.x + world.rand.nextGaussian() * range);
            int y = MathHelper.floor(powderPos.y + world.rand.nextGaussian() * range);
            int z = MathHelper.floor(powderPos.z + world.rand.nextGaussian() * range);
            BlockPos orePos = new BlockPos(x, y, z);
            if (orePos.distanceSq(powderPos.x, powderPos.y, powderPos.z) <= range * range
                    && orePos.distanceSq(pos) <= this.dist * this.dist && world.isBlockLoaded(orePos)) {
                IBlockState state = world.getBlockState(orePos);
                Block block = state.getBlock();
                if (block != requiredBlock)
                    continue;

                while (true) {
                    WeightedOre ore = WeightedRandom.getRandomItem(world.rand, ores, totalWeight);
                    List<ItemStack> stacks = OreDictionary.getOres(ore.name, false);
                    for (ItemStack stack : stacks) {
                        if (stack.isEmpty())
                            continue;
                        Block toPlace = Block.getBlockFromItem(stack.getItem());
                        if (toPlace == Blocks.AIR)
                            continue;

                        IBlockState stateToPlace = toPlace.getDefaultState();
                        world.setBlockState(orePos, stateToPlace);
                        world.playEvent(2001, orePos, Block.getStateId(stateToPlace));

                        int toDrain = (20000 - ore.itemWeight * 2) * 2;
                        BlockPos highestSpot = IAuraChunk.getHighestSpot(world, orePos, 30, pos);
                        IAuraChunk.getAuraChunk(world, highestSpot).drainAura(highestSpot, toDrain);

                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return ModConfig.enabledFeatures.oreEffect &&
                (type.isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) || type.isSimilar(NaturesAuraAPI.TYPE_NETHER));
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }
}
