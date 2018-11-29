package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class BlockStairsNA extends BlockStairs implements IModItem, ICreativeItem, IModelProvider {

    private final String baseName;

    protected BlockStairsNA(String baseName, IBlockState modelState) {
        super(modelState);
        this.baseName = baseName;
        ModRegistry.add(this);
        this.fullBlock = false;
        this.lightOpacity = 0;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
