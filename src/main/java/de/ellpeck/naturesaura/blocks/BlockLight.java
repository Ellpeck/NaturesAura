package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import de.ellpeck.naturesaura.reg.INoItemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.level.BlockGetter;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

public class BlockLight extends BlockImpl implements ICustomBlockState, INoItemBlock, ICustomRenderType {

    private static final VoxelShape SHAPE = makeCuboidShape(4, 4, 4, 12, 12, 12);

    public BlockLight() {
        super("light", Properties.create(Material.WOOL).doesNotBlockMovement().setLightLevel(s -> 15));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 2; i++)
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    rand.nextGaussian() * 0.015F, 0, rand.nextGaussian() * 0.015F,
                    0xffcb5c, rand.nextFloat() * 2 + 1, 50, -0.015F, true, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        return true;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().withExistingParent("light", generator.mcLoc("block/air"))
                .texture("particle", "block/light"));
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getCutoutMipped;
    }
}
