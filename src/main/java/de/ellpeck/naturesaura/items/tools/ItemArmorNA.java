package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.reg.ICreativeItem;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.IModelProvider;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemArmorNA extends ItemArmor implements IModItem, ICreativeItem, IModelProvider {

    private static List<Item[]> sets;
    private final String baseName;

    public ItemArmorNA(String baseName, ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, 0, equipmentSlotIn);
        this.baseName = baseName;
        ModRegistry.add(this);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote) {
            if (!isFullSetEquipped(entity, 0))
                return;
            Entity source = event.getSource().getTrueSource();
            if (source instanceof EntityLivingBase)
                ((EntityLivingBase) source).addPotionEffect(new PotionEffect(MobEffects.WITHER, 40));
        }
    }

    @Override
    public String getBaseName() {
        return this.baseName;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return Helper.makeRechargeProvider(stack, false);
    }

    public static boolean isFullSetEquipped(EntityLivingBase entity, int setIndex) {
        if (sets == null) {
            sets = new ArrayList<>();
            sets.add(new Item[]{ModItems.INFUSED_SHOES, ModItems.INFUSED_PANTS, ModItems.INFUSED_CHEST, ModItems.INFUSED_HELMET});
        }

        Item[] set = sets.get(setIndex);
        for (int i = 0; i < 4; i++) {
            EntityEquipmentSlot slot = EntityEquipmentSlot.values()[i + 2];
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.isEmpty() || stack.getItem() != set[i])
                return false;
        }
        return true;
    }
}
