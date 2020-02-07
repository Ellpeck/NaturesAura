package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.NaturesAura;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGlowing extends ItemImpl {
    public ItemGlowing(String baseName) {
        super(baseName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
