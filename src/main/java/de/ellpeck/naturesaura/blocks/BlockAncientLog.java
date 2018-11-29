package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class BlockAncientLog extends BlockLog implements IModItem, ICreativeItem, IModelProvider {

    public BlockAncientLog() {
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return "ancient_log";
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LOG_AXIS).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LOG_AXIS, EnumAxis.values()[meta]);
    }
}
