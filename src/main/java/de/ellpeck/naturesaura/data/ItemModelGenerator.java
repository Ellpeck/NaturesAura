package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.INoItemBlock;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

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
            } else if (modItem instanceof Block && !(modItem instanceof INoItemBlock)) {
                this.withExistingParent(name, this.modLoc("block/" + name));
            }
        }
    }

    @Override
    public String getName() {
        return "Nature's Aura Item Models";
    }
}
