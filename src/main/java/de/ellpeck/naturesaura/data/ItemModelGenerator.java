package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, NaturesAura.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (IModItem modItem : ModRegistry.ALL_ITEMS) {
            String name = modItem.getBaseName();
            if (modItem instanceof ICustomItemModel) {
                ((ICustomItemModel) modItem).generateCustomItemModel(this);
            } else if (modItem instanceof Item) {
                this.withExistingParent(name, "item/generated").texture("layer0", "item/" + name);
            } else if (modItem instanceof Block) {
                this.withExistingParent(name, this.modLoc("block/" + name));
            }
        }
    }

    @Override
    public String getName() {
        return "Nature's Aura Item Models";
    }
}
