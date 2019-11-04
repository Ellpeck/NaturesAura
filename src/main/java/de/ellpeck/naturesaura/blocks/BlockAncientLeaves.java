package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAncientLeaves;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAncientLeaves extends LeavesBlock implements
        IModItem, IModelProvider, IColorProvidingBlock, IColorProvidingItem {

    public BlockAncientLeaves() {
        super(ModBlocks.prop(Material.LEAVES, MaterialColor.PINK));
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return "ancient_leaves";
    }

    public void onInit(FMLCommonSetupEvent event) {
        //GameRegistry.registerTileEntity(TileEntityAncientLeaves.class, new ResourceLocation(NaturesAura.MOD_ID, "ancient_leaves"));
    }

    /*  Appears to be handled already somewhere by super
    @Override
    public void beginLeaveDecay(BlockState state, IWorldReader world, BlockPos pos) {
        if (!state.get(DISTANCE).intValue() && state.getValue(DECAYABLE)) {
            world.getChunk(pos).setBlockState(pos, state.with(CHECK_DECAY, true), false);
        }
    }
    */


    /*  // Appears to auto remove TE during setting of the state by the world
    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
     */

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityAncientLeaves();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, worldIn, pos, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (rand.nextFloat() >= 0.95F && !worldIn.getBlockState(pos.down()).isOpaqueCube(worldIn, pos)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityAncientLeaves) {
                if (((TileEntityAncientLeaves) tile).getAuraContainer(null).getStoredAura() > 0) {
                    NaturesAuraAPI.instance().spawnMagicParticle(
                            pos.getX() + rand.nextDouble(), pos.getY(), pos.getZ() + rand.nextDouble(),
                            0F, 0F, 0F, 0xCC4780,
                            rand.nextFloat() * 2F + 0.5F,
                            rand.nextInt(50) + 75,
                            rand.nextFloat() * 0.02F + 0.002F, true, true);

                }
            }
        }
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        super.tick(state, worldIn, pos, random);
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityAncientLeaves) {
                if (((TileEntityAncientLeaves) tile).getAuraContainer(null).getStoredAura() <= 0) {
                    worldIn.setBlockState(pos, ModBlocks.DECAYED_LEAVES.getDefaultState());
                }
            }
        }
    }
}
