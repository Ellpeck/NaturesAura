package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityFurnaceHeater;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFurnaceHeater extends BlockContainerImpl {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(2 / 16F, 0F, 2 / 16F, 14 / 16F, 4 / 16F, 14 / 16F);

    public BlockFurnaceHeater() {
        super(Material.ROCK, "furnace_heater", TileEntityFurnaceHeater.class, "furnace_heater");
        this.setHardness(3F);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityFurnaceHeater && ((TileEntityFurnaceHeater) tile).isActive) {
            NaturesAura.proxy.spawnMagicParticle(worldIn,
                    pos.getX() + 0.35F + rand.nextFloat() * 0.3F,
                    pos.getY() + 0.2F,
                    pos.getZ() + 0.35F + rand.nextFloat() * 0.3F,
                    0F, rand.nextFloat() * 0.008F + 0.005F, 0F,
                    rand.nextBoolean() ? 0xf46e42 : 0xf49541, rand.nextFloat() + 0.5F, 100, 0F, true, true);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
