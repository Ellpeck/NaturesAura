package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAuraBloom;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.function.Supplier;

public class BlockAuraBloom extends BushBlock implements IModItem, ICustomBlockState, ICustomItemModel, ICustomRenderType, EntityBlock {

    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final String baseName;
    private final Block[] allowedGround;

    public BlockAuraBloom(String baseName, Block... allowedGround) {
        super(Properties.of(Material.PLANT).noCollission().strength(0).sound(SoundType.GRASS));
        this.baseName = baseName;
        this.allowedGround = allowedGround;
        ModRegistry.add(this);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelIn, BlockPos pos) {
        BlockPos down = pos.below();
        return this.mayPlaceOn(levelIn.getBlockState(down), levelIn, down);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter levelIn, BlockPos pos) {
        return Arrays.stream(this.allowedGround).anyMatch(state::is);
    }

    @Override
    public void entityInside(BlockState state, Level levelIn, BlockPos pos, Entity entityIn) {
        if (this == ModBlocks.AURA_CACTUS)
            entityIn.hurt(DamageSource.CACTUS, 1);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        Vec3 vec3d = state.getOffset(levelIn, pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
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

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityAuraBloom();
    }
}
