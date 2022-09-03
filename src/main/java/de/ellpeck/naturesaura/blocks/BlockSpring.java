package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySpring;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class BlockSpring extends BlockContainerImpl implements ICustomBlockState, IColorProvidingBlock, IColorProvidingItem, BucketPickup {

    public BlockSpring() {
        super("spring", BlockEntitySpring.class, Properties.copy(Blocks.STONE_BRICKS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockColor getBlockColor() {
        return (state, level, pos, i) -> BiomeColors.getAverageWaterColor(level, pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("Convert2Lambda")
    public ItemColor getItemColor() {
        return new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int i) {
                Player player = Minecraft.getInstance().player;
                if (player == null)
                    return 0;
                return BiomeColors.getAverageWaterColor(player.level, player.blockPosition());
            }
        };
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor levelIn, BlockPos pos, BlockState state) {
        var tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntitySpring)
            ((BlockEntitySpring) tile).consumeAura(2500);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}
