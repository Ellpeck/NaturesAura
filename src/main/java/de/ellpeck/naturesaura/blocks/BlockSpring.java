package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.blocks.tiles.TileEntitySpring;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class BlockSpring extends BlockContainerImpl implements ICustomBlockState, IColorProvidingBlock, IColorProvidingItem, IBucketPickupHandler, ICustomRenderType {
    public BlockSpring() {
        super("spring", TileEntitySpring::new, Properties.from(Blocks.STONE_BRICKS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, i) -> BiomeColors.getWaterColor(world, pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("Convert2Lambda")
    public IItemColor getItemColor() {
        return new IItemColor() {
            @Override
            public int getColor(ItemStack stack, int i) {
                PlayerEntity player = Minecraft.getInstance().player;
                return BiomeColors.getWaterColor(player.world, player.getPosition());
            }
        };
    }

    @Override
    public void generateCustomBlockState(BlockStateGenerator generator) {
        generator.simpleBlock(this, generator.models().getExistingFile(generator.modLoc(this.getBaseName())));
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntitySpring)
            ((TileEntitySpring) tile).consumeAura(2500);
        return Fluids.WATER;
    }

    @Override
    public Supplier<RenderType> getRenderType() {
        return RenderType::getTranslucent;
    }
}
