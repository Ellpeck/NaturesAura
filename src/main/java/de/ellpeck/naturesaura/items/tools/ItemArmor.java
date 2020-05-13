package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class ItemArmor extends ArmorItem implements IModItem {

    private static final AttributeModifier SKY_MOVEMENT_MODIFIER = new AttributeModifier(UUID.fromString("c1f96acc-e117-4dc1-a351-e196a4de6071"), NaturesAura.MOD_ID + ":sky_movement_speed", 0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final Map<IArmorMaterial, Item[]> SETS = new HashMap<>();
    private final String baseName;

    public ItemArmor(String baseName, IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn) {
        super(materialIn, equipmentSlotIn, new Properties().group(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ModRegistry.add(this);
    }

    public static boolean isFullSetEquipped(LivingEntity entity, IArmorMaterial material) {
        Item[] set = SETS.computeIfAbsent(material, m -> ForgeRegistries.ITEMS.getValues().stream()
                .filter(i -> i instanceof ItemArmor && ((ItemArmor) i).getArmorMaterial() == material)
                .sorted(Comparator.comparingInt(i -> ((ItemArmor) i).getEquipmentSlot().ordinal()))
                .toArray(Item[]::new));
        for (int i = 0; i < 4; i++) {
            EquipmentSlotType slot = EquipmentSlotType.values()[i + 2];
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.isEmpty() || stack.getItem() != set[i])
                return false;
        }
        return true;
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

    private static class EventHandler {

        @SubscribeEvent
        public void onAttack(LivingAttackEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (!entity.world.isRemote) {
                if (!isFullSetEquipped(entity, ModArmorMaterial.INFUSED))
                    return;
                Entity source = event.getSource().getTrueSource();
                if (source instanceof LivingEntity)
                    ((LivingEntity) source).addPotionEffect(new EffectInstance(Effects.WITHER, 40));
            }
        }

        @SubscribeEvent
        public void update(TickEvent.PlayerTickEvent event) {
            PlayerEntity player = event.player;
            IAttributeInstance speed = player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            String key = NaturesAura.MOD_ID + ":sky_equipped";
            CompoundNBT nbt = player.getPersistentData();
            boolean equipped = isFullSetEquipped(player, ModArmorMaterial.SKY);
            if (equipped && !nbt.getBoolean(key)) {
                // we just equipped it
                nbt.putBoolean(key, true);
                player.stepHeight = 1.1F;
                if (!speed.hasModifier(SKY_MOVEMENT_MODIFIER))
                    speed.applyModifier(SKY_MOVEMENT_MODIFIER);
            } else if (!equipped && nbt.getBoolean(key)) {
                // we just unequipped it
                nbt.putBoolean(key, false);
                player.stepHeight = 0.6F;
                speed.removeModifier(SKY_MOVEMENT_MODIFIER);
            }
        }
    }
}
