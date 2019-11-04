package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import net.minecraft.item.Item;

public class ItemImpl extends Item implements IModItem, IModelProvider {

    private final String baseName;

    public ItemImpl(String baseName) {
        this(baseName, new Properties().group(NaturesAura.CREATIVE_TAB));
    }

    public ItemImpl(String baseName, Item.Properties properties) {
        super(properties);
        this.baseName = baseName;
        this.setRegistryName(NaturesAura.createRes(this.getBaseName()));
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }
}
