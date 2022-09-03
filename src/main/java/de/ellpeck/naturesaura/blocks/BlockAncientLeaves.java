package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAncientLeaves;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.*;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class BlockAncientLeaves extends LeavesBlock implements IModItem, IColorProvidingBlock, IColorProvidingItem, ICustomBlockState, EntityBlock {

    public BlockAncientLeaves() {
        super(Block.Properties.of(Material.LEAVES, MaterialColor.COLOR_PINK).strength(0.2F).randomTicks().noOcclusion().sound(SoundType.GRASS));
        ModRegistry.ALL_ITEMS.add(this);
        ModRegistry.ALL_ITEMS.add(new ModTileType<>(BlockEntityAncientLeaves::new, this));
    }

    @Override
    public String getBaseName() {
        return "ancient_leaves";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockColor getBlockColor() {
        return (state, levelIn, pos, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> 0xE55B97;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos pos, RandomSource rand) {
        super.animateTick(stateIn, levelIn, pos, rand);
        if (rand.nextFloat() >= 0.95F && !levelIn.getBlockState(pos.below()).isCollisionShapeFullBlock(levelIn, pos)) {
            var tile = levelIn.getBlockEntity(pos);
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
    public void randomTick(BlockState state, ServerLevel levelIn, BlockPos pos, RandomSource random) {
        super.randomTick(state, levelIn, pos, random);
        if (!levelIn.isClientSide) {
            var tile = levelIn.getBlockEntity(pos);
            if (tile instanceof BlockEntityAncientLeaves) {
                if (((BlockEntityAncientLeaves) tile).getAuraContainer().getStoredAura() <= 0) {
                    levelIn.setBlockAndUpdate(pos, ModBlocks.DECAYED_LEAVES.defaultBlockState());
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityAncientLeaves(pos, state);
    }
}
