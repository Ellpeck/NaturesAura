package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemArmor extends ArmorItem implements IModItem {

    private static final AttributeModifier SKY_MOVEMENT_MODIFIER = new AttributeModifier(UUID.fromString("c1f96acc-e117-4dc1-a351-e196a4de6071"), NaturesAura.MOD_ID + ":sky_movement_speed", 0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final Map<ArmorMaterial, Item[]> SETS = new HashMap<>();
    private final String baseName;

    public ItemArmor(String baseName, ArmorMaterial materialIn, EquipmentSlot equipmentSlotIn) {
        super(materialIn, equipmentSlotIn, new Properties().tab(NaturesAura.CREATIVE_TAB));
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    public static boolean isFullSetEquipped(LivingEntity entity, ArmorMaterial material) {
        var set = ItemArmor.SETS.computeIfAbsent(material, m -> ForgeRegistries.ITEMS.getValues().stream()
                .filter(i -> i instanceof ItemArmor && ((ItemArmor) i).getMaterial() == material)
                .sorted(Comparator.comparingInt(i -> ((ItemArmor) i).getSlot().ordinal()))
                .toArray(Item[]::new));
        for (var i = 0; i < 4; i++) {
            var slot = EquipmentSlot.values()[i + 2];
            var stack = entity.getItemBySlot(slot);
            if (stack.getItem() != set[i] && (slot != EquipmentSlot.CHEST || stack.getItem() != Items.ELYTRA))
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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return Helper.makeRechargeProvider(stack, false);
    }

    @Mod.EventBusSubscriber
    private static final class EventHandler {

        @SubscribeEvent
        public static void onAttack(LivingAttackEvent event) {
            var entity = event.getEntityLiving();
            if (!entity.level.isClientSide) {
                if (!ItemArmor.isFullSetEquipped(entity, ModArmorMaterial.INFUSED))
                    return;
                var source = event.getSource().getEntity();
                if (source instanceof LivingEntity)
                    ((LivingEntity) source).addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
        }

        @SubscribeEvent
        public static void update(TickEvent.PlayerTickEvent event) {
            var player = event.player;
            var speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            var key = NaturesAura.MOD_ID + ":sky_equipped";
            var nbt = player.getPersistentData();
            var equipped = ItemArmor.isFullSetEquipped(player, ModArmorMaterial.SKY);
            if (equipped && !nbt.getBoolean(key)) {
                // we just equipped it
                nbt.putBoolean(key, true);
                player.maxUpStep = 1.1F;
                if (!speed.hasModifier(ItemArmor.SKY_MOVEMENT_MODIFIER))
                    speed.addPermanentModifier(ItemArmor.SKY_MOVEMENT_MODIFIER);
            } else if (!equipped && nbt.getBoolean(key)) {
                // we just unequipped it
                nbt.putBoolean(key, false);
                player.maxUpStep = 0.6F;
                speed.removeModifier(ItemArmor.SKY_MOVEMENT_MODIFIER);
            }
        }
    }
}
