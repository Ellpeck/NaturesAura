package de.ellpeck.naturesaura.reg;

import net.minecraft.client.color.item.ItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IColorProvidingItem {

    @OnlyIn(Dist.CLIENT)
    ItemColor getItemColor();

}
