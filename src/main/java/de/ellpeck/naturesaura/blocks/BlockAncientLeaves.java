package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAncientLeaves;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraft.level.server.ServerLevel;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAncientLeaves extends LeavesBlock implements IModItem, IColorProvidingBlock, IColorProvidingItem, ICustomBlockState {

    public BlockAncientLeaves() {
        super(Block.Properties.of(Material.LEAVES, MaterialColor.COLOR_PINK).strength(0.2F).randomTicks().noOcclusion().sound(SoundType.GRASS));
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(BlockEntityAncientLeaves::new, this));
    }

    @Override
    public String getBaseName() {
        return "ancient_leaves";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader level) {
        return new BlockEntityAncientLeaves();
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, levelIn, pos, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, levelIn, pos, rand);
        if (rand.nextFloat() >= 0.95F && !levelIn.getBlockState(pos.down()).isOpaqueCube(levelIn, pos)) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityAncientLeaves) {
                if (((BlockEntityAncientLeaves) tile).getAuraContainer().getStoredAura() > 0) {
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
    public void randomTick(BlockState state, ServerLevel levelIn, BlockPos pos, Random random) {
        super.randomTick(state, levelIn, pos, random);
        if (!levelIn.isClientSide) {
            BlockEntity tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityAncientLeaves) {
                if (((BlockEntityAncientLeaves) tile).getAuraContainer().getStoredAura() <= 0) {
                    levelIn.setBlockState(pos, ModBlocks.DECAYED_LEAVES.getDefaultState());
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
