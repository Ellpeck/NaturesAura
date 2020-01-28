package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class ItemHoe extends HoeItem implements IModItem, ICustomItemModel {

    private final String baseName;

    public ItemHoe(String baseName, IItemTier material, float speed) {
        super(material, speed, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.add(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        ActionResultType result = super.onItemUse(context);
        if (!world.isRemote && result == ActionResultType.SUCCESS && this == ModItems.INFUSED_IRON_HOE) {
            ItemStack seed = ItemStack.EMPTY;
            Random random = world.getRandom();
            BlockPos pos = context.getPos();
            if (random.nextInt(5) == 0) {
                seed = new ItemStack(Items.WHEAT_SEEDS);
            } else if (random.nextInt(10) == 0) {
                int rand = random.nextInt(3);
                if (rand == 0) {
                    seed = new ItemStack(Items.MELON_SEEDS);
                } else if (rand == 1) {
                    seed = new ItemStack(Items.PUMPKIN_SEEDS);
                } else if (rand == 2) {
                    seed = new ItemStack(Items.BEETROOT_SEEDS);
                }
            }

            if (!seed.isEmpty()) {
                ItemEntity item = new ItemEntity(world, pos.getX() + random.nextFloat(), pos.getY() + 1F, pos.getZ() + random.nextFloat(), seed);
                world.addEntity(item);
            }
        }
        return result;
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (this == ModItems.INFUSED_IRON_HOE)
            return Helper.makeRechargeProvider(stack, true);
        else return null;
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/handheld").texture("layer0", "item/" + this.getBaseName());
    }

}
