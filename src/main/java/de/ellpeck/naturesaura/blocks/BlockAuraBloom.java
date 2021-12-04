package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraBloom;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevelReader;
import net.minecraft.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public class BlockAuraBloom extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final String baseName;
    private final Block[] allowedGround;

    public BlockAuraBloom(String baseName, Block... allowedGround) {
        super(Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT));
        this.baseName = baseName;
        this.allowedGround = allowedGround;
        ModRegistry.add(this);
    }

    @Override
    public boolean isValidPosition(BlockState state, ILevelReader levelIn, BlockPos pos) {
        BlockPos down = pos.down();
        return this.isValidGround(levelIn.getBlockState(down), levelIn, down);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader levelIn, BlockPos pos) {
        return Arrays.stream(this.allowedGround).anyMatch(state::isIn);
    }

    @Override
    public void onEntityCollision(BlockState state, Level levelIn, BlockPos pos, Entity entityIn) {
        if (this == ModBlocks.AURA_CACTUS)
            entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
        Vector3d vec3d = state.getOffset(levelIn, pos);
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
        return RenderType::getCutout;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader level) {
        return new BlockEntityAuraBloom();
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }
}
