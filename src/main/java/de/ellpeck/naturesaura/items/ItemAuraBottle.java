package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemAuraBottle extends ItemImpl implements IColorProvidingItem, ICustomItemModel {

    public ItemAuraBottle(Item emptyBottle) {
        super("aura_bottle");
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        DispenserBlock.registerBehavior(emptyBottle, (source, stack) -> {
            Level level = source.getLevel();
            var state = source.getBlockState();
            var facing = state.getValue(DispenserBlock.FACING);
            var offset = source.getPos().relative(facing);
            var offsetState = level.getBlockState(offset);

            var dispense = stack.split(1);
            if (offsetState.isAir()) {
                if (IAuraChunk.getAuraInArea(level, offset, 30) >= 100000) {
                    dispense = setType(new ItemStack(ItemAuraBottle.this), IAuraType.forLevel(level));

                    var spot = IAuraChunk.getHighestSpot(level, offset, 30, offset);
                    IAuraChunk.getAuraChunk(level, spot).drainAura(spot, 20000);
                }
            }

            DefaultDispenseItemBehavior.spawnItem(level, dispense, 6, facing, DispenserBlock.getDispensePosition(source));
            return stack;
        });
    }

    public static IAuraType getType(ItemStack stack) {
        if (!stack.hasTag())
            return NaturesAuraAPI.TYPE_OTHER;
        var type = stack.getTag().getString("stored_type");
        if (type.isEmpty())
            return NaturesAuraAPI.TYPE_OTHER;
        return NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(type));
    }

    public static ItemStack setType(ItemStack stack, IAuraType type) {
        stack.getOrCreateTag().putString("stored_type", type.getName().toString());
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (var type : NaturesAuraAPI.AURA_TYPES.values()) {
                var stack = new ItemStack(this);
                setType(stack, type);
                items.add(stack);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent(stack.getDescriptionId() + "." + getType(stack).getName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> tintIndex > 0 ? getType(stack).getColor() : 0xFFFFFF;
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated")
                .texture("layer0", "item/" + this.getBaseName())
                .texture("layer1", "item/" + this.getBaseName() + "_overlay");
    }

    private class EventHandler {

        @SubscribeEvent
        public void onRightClick(PlayerInteractEvent.RightClickItem event) {
            var held = event.getItemStack();
            if (held.isEmpty() || held.getItem() != ModItems.BOTTLE_TWO_THE_REBOTTLING)
                return;
            var player = event.getPlayer();
            HitResult ray = getPlayerPOVHitResult(player.level, player, ClipContext.Fluid.NONE);
            if (ray.getType() == HitResult.Type.BLOCK)
                return;
            var pos = player.blockPosition();
            if (IAuraChunk.getAuraInArea(player.level, pos, 30) < 100000)
                return;

            if (!player.level.isClientSide) {
                held.shrink(1);

                player.getInventory().add(setType(new ItemStack(ItemAuraBottle.this), IAuraType.forLevel(player.level)));

                var spot = IAuraChunk.getHighestSpot(player.level, pos, 30, pos);
                IAuraChunk.getAuraChunk(player.level, spot).drainAura(spot, 20000);

                player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1F, 1F);
            }
            player.swing(event.getHand());
        }

    }
}
