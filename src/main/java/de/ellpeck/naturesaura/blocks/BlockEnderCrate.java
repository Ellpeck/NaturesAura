package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityEnderCrate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEnderCrate extends BlockContainerImpl {
    public BlockEnderCrate() {
        super(Material.ROCK, "ender_crate", TileEntityEnderCrate.class, "ender_crate");
        this.setSoundType(SoundType.STONE);
        this.setHardness(5F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityEnderCrate) {
                TileEntityEnderCrate crate = (TileEntityEnderCrate) tile;
                if (crate.canOpen())
                    playerIn.openGui(NaturesAura.MOD_ID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
