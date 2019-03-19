package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityChunkLoader;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockChunkLoader extends BlockContainerImpl implements IVisualizable {

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
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return 0xc159f9;
    }

    public static class ChunkLoadingCallback implements ForgeChunkManager.LoadingCallback {

        @Override
        public void ticketsLoaded(List<Ticket> tickets, World world) {
            for (Ticket ticket : tickets) {
                NBTTagCompound data = ticket.getModData();
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
