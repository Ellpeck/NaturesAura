package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityAncientLeaves;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAncientLeaves extends LeavesBlock implements IModItem, IColorProvidingBlock, IColorProvidingItem, ICustomBlockState {

    public BlockAncientLeaves() {
        super(Properties.create(Material.LEAVES, MaterialColor.PINK).hardnessAndResistance(0.2F).tickRandomly().notSolid().sound(SoundType.PLANT));
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(TileEntityAncientLeaves::new, this));
    }

    @Override
    public String getBaseName() {
        return "ancient_leaves";
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityAncientLeaves();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
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
                if (((TileEntityAncientLeaves) tile).getAuraContainer().getStoredAura() > 0) {
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
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityAncientLeaves) {
                if (((TileEntityAncientLeaves) tile).getAuraContainer().getStoredAura() <= 0) {
                    worldIn.setBlockState(pos, ModBlocks.DECAYED_LEAVES.getDefaultState());
                }
            }
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
