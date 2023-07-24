package de.ellpeck.naturesaura.data;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.INoItemBlock;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NaturesAura.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var modItem : ModRegistry.ALL_ITEMS) {
            var name = modItem.getBaseName();
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
