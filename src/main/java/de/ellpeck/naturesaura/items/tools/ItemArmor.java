package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemArmor extends ArmorItem implements IModItem, IModelProvider {

    private static List<Item[]> sets;
    private final String baseName;

    public ItemArmor(String baseName, IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn) {
        super(materialIn, equipmentSlotIn, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ModRegistry.add(this);
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return Helper.makeRechargeProvider(stack, false);
    }

    public static boolean isFullSetEquipped(LivingEntity entity, int setIndex) {
        if (sets == null) {
            sets = new ArrayList<>();
            sets.add(new Item[]{ModItems.INFUSED_IRON_SHOES, ModItems.INFUSED_IRON_PANTS, ModItems.INFUSED_IRON_CHEST, ModItems.INFUSED_IRON_HELMET});
        }

        Item[] set = sets.get(setIndex);
        for (int i = 0; i < 4; i++) {
            EquipmentSlotType slot = EquipmentSlotType.values()[i + 2];
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.isEmpty() || stack.getItem() != set[i])
                return false;
        }
        return true;
    }

    private static class EventHandler {

        @SubscribeEvent
        public void onAttack(LivingAttackEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (!entity.world.isRemote) {
                if (!isFullSetEquipped(entity, 0))
                    return;
                Entity source = event.getSource().getTrueSource();
                if (source instanceof LivingEntity)
                    ((LivingEntity) source).addPotionEffect(new EffectInstance(Effects.WITHER, 40));
            }
        }
    }
}
