package de.ellpeck.naturesaura.reg;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.creativetab.CreativeTabs;

public interface ICreativeItem {

    default CreativeTabs getTabToAdd() {
        return NaturesAura.CREATIVE_TAB;
    }
}
