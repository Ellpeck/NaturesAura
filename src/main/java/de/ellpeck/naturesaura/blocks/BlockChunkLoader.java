package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityChunkLoader;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

public class BlockChunkLoader extends BlockContainerImpl implements IVisualizable {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(4 / 16F, 4 / 16F, 4 / 16F, 12 / 16F, 12 / 16F, 12 / 16F);

    public BlockChunkLoader() {
        super(Material.ROCK, "chunk_loader", TileEntityChunkLoader.class, "chunk_loader");
        this.setSoundType(SoundType.STONE);
        this.setHardness(3F);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        ForgeChunkManager.setForcedChunkLoadingCallback(NaturesAura.instance, new ChunkLoadingCallback());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityChunkLoader) {
            int range = ((TileEntityChunkLoader) tile).range();
            if (range > 0) {
                return new AxisAlignedBB(
                        (pos.getX() - range) >> 4 << 4,
                        0,
                        (pos.getZ() - range) >> 4 << 4,
                        ((pos.getX() + range) >> 4 << 4) + 16,
                        world.getHeight(),
                        ((pos.getZ() + range) >> 4 << 4) + 16);
            }
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityChunkLoader) {
            int range = ((TileEntityChunkLoader) tile).range();
            for (int i = MathHelper.ceil(range / 8F); i > 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        pos.getX() + worldIn.rand.nextFloat(), pos.getY() + worldIn.rand.nextFloat(), pos.getZ() + worldIn.rand.nextFloat(),
                        0, 0, 0, 0xa12dff, 1F + worldIn.rand.nextFloat(), 100, 0, false, true);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xc159f9;
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }

    public static class ChunkLoadingCallback implements ForgeChunkManager.LoadingCallback {

        @Override
        public void ticketsLoaded(List<Ticket> tickets, World world) {
            for (Ticket ticket : tickets) {
                CompoundNBT data = ticket.getModData();
                BlockPos pos = BlockPos.fromLong(data.getLong("pos"));
                TileEntity tile = world.getTileEntity(pos);
                if (!(tile instanceof TileEntityChunkLoader))
                    continue;
                TileEntityChunkLoader loader = (TileEntityChunkLoader) tile;
                loader.updateTicket(ticket);
                loader.loadChunks();
            }
        }
    }
}
