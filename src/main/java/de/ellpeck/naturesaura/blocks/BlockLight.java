package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.INoItemBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class BlockLight extends BlockImpl implements ICustomBlockState, INoItemBlock {

    private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

    public BlockLight() {
        super("light", Properties.of(Material.WOOL).noCollission().lightLevel(s -> 15));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        for (var i = 0; i < 2; i++)
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    rand.nextGaussian() * 0.015F, 0, rand.nextGaussian() * 0.015F,
                    0xffcb5c, rand.nextFloat() * 2 + 1, 50, -0.015F, true, true);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return BlockLight.SHAPE;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return true;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().withExistingParent("light", generator.mcLoc("block/air"))
                .renderType("cutout_mipped").texture("particle", "block/light"));
    }
}
