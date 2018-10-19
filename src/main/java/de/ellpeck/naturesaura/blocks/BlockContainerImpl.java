package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class BlockContainerImpl extends BlockContainer implements IModItem, IModelProvider {

    private final String baseName;

    private final Class<? extends TileEntity> tileClass;
    private final String tileRegName;

    public BlockContainerImpl(Material material, String baseName, Class<? extends TileEntity> tileClass, String tileReg) {
        super(material);

        this.baseName = baseName;
        this.tileClass = tileClass;
        this.tileRegName = tileReg;

        ModRegistry.addItemOrBlock(this);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        try {
            return this.tileClass.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public boolean shouldAddCreative() {
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(this.tileClass, new ResourceLocation(NaturesAura.MOD_ID, this.tileRegName));
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityImpl) {
                IItemHandler handler = ((TileEntityImpl) tile).getItemHandler(null);
                if (handler != null) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                            worldIn.spawnEntity(item);
                        }
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}