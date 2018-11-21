package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITESRProvider {

    @SideOnly(Side.CLIENT)
    Tuple<Class, TileEntitySpecialRenderer> getTESR();

}
