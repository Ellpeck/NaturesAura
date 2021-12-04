package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.world.item.Item;

public class ItemImpl extends Item implements IModItem {

    private final String baseName;

    public ItemImpl(String baseName) {
        this(baseName, new Properties());
    }

    public ItemImpl(String baseName, Item.Properties properties) {
        super(properties.tab(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
