package de.ellpeck.naturesaura.items.tools;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.reg.IModItem;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import de.ellpeck.naturesaura.reg.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemArmor extends ArmorItem implements IModItem {

    private static final AttributeModifier SKY_MOVEMENT_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "sky_movement_speed"), 0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final AttributeModifier SKY_STEP_MODIFIER = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(NaturesAura.MOD_ID, "sky_step_modifier"), 0.5F, AttributeModifier.Operation.ADD_VALUE);
    private static final Map<ArmorMaterial, Item[]> SETS = new ConcurrentHashMap<>();
    private final String baseName;

    public ItemArmor(String baseName, ModArmorMaterial materialIn, ArmorItem.Type equipmentSlotIn) {
        super(materialIn.material, equipmentSlotIn, new Properties().durability(materialIn.getDurability(equipmentSlotIn)));
        this.baseName = baseName;
        ModRegistry.ALL_ITEMS.add(this);
    }

    public static boolean isFullSetEquipped(LivingEntity entity, ArmorMaterial material) {
        var set = ItemArmor.SETS.computeIfAbsent(material, m -> BuiltInRegistries.ITEM.stream()
            .filter(i -> i instanceof ItemArmor && ((ItemArmor) i).getMaterial().value() == material)
            .sorted(Comparator.comparingInt(i -> ((ItemArmor) i).getEquipmentSlot().ordinal()))
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

    @EventBusSubscriber
    private static final class EventHandler {

        @SubscribeEvent
        public static void onAttack(LivingIncomingDamageEvent event) {
            var entity = event.getEntity();
            if (!entity.level().isClientSide) {
                if (ItemArmor.isFullSetEquipped(entity, ModArmorMaterial.INFUSED.material.value())) {
                    var source = event.getSource().getEntity();
                    if (source instanceof LivingEntity)
                        ((LivingEntity) source).addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
                } else if (ItemArmor.isFullSetEquipped(entity, ModArmorMaterial.DEPTH.material.value())) {
                    for (var other : entity.level().getEntitiesOfClass(LivingEntity.class, new AABB(entity.position(), entity.position()).inflate(2))) {
                        if (other != entity && (!(entity instanceof Player player) || !player.isAlliedTo(other)))
                            other.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 255));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void update(PlayerTickEvent.Post event) {
            var player = event.getEntity();
            var speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            var step = player.getAttribute(Attributes.STEP_HEIGHT);
            var key = NaturesAura.MOD_ID + ":sky_equipped";
            var nbt = player.getPersistentData();
            var equipped = ItemArmor.isFullSetEquipped(player, ModArmorMaterial.SKY.material.value());
            if (equipped && !nbt.getBoolean(key)) {
                // we just equipped it
                nbt.putBoolean(key, true);
                if (!step.hasModifier(ItemArmor.SKY_STEP_MODIFIER.id()))
                    step.addPermanentModifier(ItemArmor.SKY_STEP_MODIFIER);
                if (!speed.hasModifier(ItemArmor.SKY_MOVEMENT_MODIFIER.id()))
                    speed.addPermanentModifier(ItemArmor.SKY_MOVEMENT_MODIFIER);
            } else if (!equipped && nbt.getBoolean(key)) {
                // we just unequipped it
                nbt.putBoolean(key, false);
                step.removeModifier(ItemArmor.SKY_STEP_MODIFIER.id());
                speed.removeModifier(ItemArmor.SKY_MOVEMENT_MODIFIER.id());
            }
        }

    }

}
