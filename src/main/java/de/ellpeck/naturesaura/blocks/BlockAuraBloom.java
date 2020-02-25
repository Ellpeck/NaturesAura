package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockAuraBloom extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final String baseName;
    private final Supplier<TileEntity> tileEntitySupplier;

    public BlockAuraBloom(String baseName, Supplier<TileEntity> tileEntitySupplier) {
        super(ModBlocks.prop(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT));
        this.baseName = baseName;
        this.tileEntitySupplier = tileEntitySupplier;
        ModRegistry.add(this);
        ModRegistry.add(new ModTileType<>(this.tileEntitySupplier, this));
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        if (this == ModBlocks.AURA_CACTUS)
            return worldIn.getBlockState(pos.down()).getBlock() instanceof SandBlock;
        return super.isValidPosition(state, worldIn, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (this == ModBlocks.AURA_CACTUS)
            entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vec3d vec3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().cross(this.getBaseName(), generator.modLoc("block/" + this.getBaseName())));
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated").texture("layer0", "block/" + this.getBaseName());
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::cutout;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntitySupplier.get();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
