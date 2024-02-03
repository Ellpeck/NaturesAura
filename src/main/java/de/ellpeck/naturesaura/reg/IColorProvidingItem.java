package de.ellpeck.naturesaura.reg;

import net.minecraft.client.color.item.ItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IColorProvidingItem {

    @OnlyIn(Dist.CLIENT)
    ItemColor getItemColor();

}
