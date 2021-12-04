package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.ModConfig;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityChunkLoader;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockChunkLoader extends BlockContainerImpl implements IVisualizable, ICustomBlockState {

    private static final VoxelShape SHAPE = makeCuboidShape(4, 4, 4, 12, 12, 12);

    public BlockChunkLoader() {
        super("chunk_loader", BlockEntityChunkLoader::new, Properties.create(Material.ROCK).hardnessAndResistance(3F).sound(SoundType.STONE));
    }

    @Override
    protected boolean hasWaterlogging() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getVisualizationBounds(Level level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof BlockEntityChunkLoader) {
            int range = ((BlockEntityChunkLoader) tile).range();
            if (range > 0) {
                return new AxisAlignedBB(
                        (pos.getX() - range) >> 4 << 4,
                        0,
                        (pos.getZ() - range) >> 4 << 4,
                        ((pos.getX() + range) >> 4 << 4) + 16,
                        level.getHeight(),
                        ((pos.getZ() + range) >> 4 << 4) + 16);
            }
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        if (!ModConfig.instance.chunkLoader.get())
            return;
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntityChunkLoader) {
            int range = ((BlockEntityChunkLoader) tile).range();
            for (int i = Mth.ceil(range / 8F); i > 0; i--) {
                NaturesAuraAPI.instance().spawnMagicParticle(
                        pos.getX() + levelIn.rand.nextFloat(), pos.getY() + levelIn.rand.nextFloat(), pos.getZ() + levelIn.rand.nextFloat(),
                        0, 0, 0, 0xa12dff, 1F + levelIn.rand.nextFloat(), 100, 0, false, true);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 0xc159f9;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public String getTranslationKey() {
        return ModConfig.instance.chunkLoader.get() ? super.getTranslationKey() : "block." + NaturesAura.MOD_ID + "." + this.getBaseName() + ".disabled";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader levelIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, levelIn, tooltip, flagIn);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }
}
