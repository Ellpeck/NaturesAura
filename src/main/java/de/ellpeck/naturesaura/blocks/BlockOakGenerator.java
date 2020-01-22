package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.ModTileEntities;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityOakGenerator;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class BlockOakGenerator extends BlockContainerImpl implements IVisualizable {

    public BlockOakGenerator() {
        super("oak_generator", TileEntityOakGenerator::new, ModBlocks.prop(Material.WOOD).hardnessAndResistance(2F).sound(SoundType.WOOD));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote() && IAuraType.forWorld(world).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)
                && world.getBlockState(pos).getBlock() instanceof SaplingBlock) {
            Helper.getTileEntitiesInArea(world, pos, 10, tile -> {
                if (!(tile instanceof TileEntityOakGenerator))
                    return false;

                Random rand = event.getRand();
                if (rand.nextInt(10) == 0)
                    ((TileEntityOakGenerator) tile).scheduledBigTrees.add(pos);

                long seed;
                do {
                    seed = rand.nextLong();
                    rand.setSeed(seed);
                }
                while (rand.nextInt(10) == 0);
                rand.setSeed(seed);

                return true;
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(10);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x2e7a11;
    }
}
