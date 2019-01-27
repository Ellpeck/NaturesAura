package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.IVisualizableBlock;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityOakGenerator;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockOakGenerator extends BlockContainerImpl implements IVisualizableBlock {

    public BlockOakGenerator() {
        super(Material.WOOD, "oak_generator", TileEntityOakGenerator.class, "oak_generator");
        this.setHardness(2F);
        this.setSoundType(SoundType.WOOD);

        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote && NaturesAuraAPI.TYPE_OVERWORLD.isPresentInWorld(world)
                && world.getBlockState(pos).getBlock() instanceof BlockSapling) {
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
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return new AxisAlignedBB(pos).grow(10);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0x2e7a11;
    }
}
