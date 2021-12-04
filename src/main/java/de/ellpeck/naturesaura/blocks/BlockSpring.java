package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySpring;
import de.ellpeck.naturesaura.data.BlockStateGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomBlockState;
import de.ellpeck.naturesaura.reg.ICustomRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.Player;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.ILevel;
import net.minecraft.level.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class BlockSpring extends BlockContainerImpl implements ICustomBlockState, IColorProvidingBlock, IColorProvidingItem, IBucketPickupHandler, ICustomRenderType {
    public BlockSpring() {
        super("spring", BlockEntitySpring::new, Properties.from(Blocks.STONE_BRICKS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, level, pos, i) -> BiomeColors.getWaterColor(level, pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("Convert2Lambda")
    public IItemColor getItemColor() {
        return new IItemColor() {
            @Override
            public int getColor(ItemStack stack, int i) {
                Player player = Minecraft.getInstance().player;
                return BiomeColors.getWaterColor(player.level, player.getPosition());
            }
        };
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public Fluid pickupFluid(ILevel levelIn, BlockPos pos, BlockState state) {
        BlockEntity tile = levelIn.getBlockEntity(pos);
        if (tile instanceof BlockEntitySpring)
            ((BlockEntitySpring) tile).consumeAura(2500);
        return Fluids.WATER;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getTranslucent;
    }
}
