package de.ellpeck.naturesaura.reg;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IColorProvidingItem {

    @SideOnly(Side.CLIENT)
    IItemColor getItemColor();

}
