package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.item.ItemGroup;

public interface ICreativeItem {

    default ItemGroup getTabToAdd() {
        return NaturesAura.CREATIVE_TAB;
    }
}
