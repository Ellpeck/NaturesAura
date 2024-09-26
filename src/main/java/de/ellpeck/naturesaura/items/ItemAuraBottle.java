package de.ellpeck.naturesaura.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomCreativeTab;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public class ItemAuraBottle extends ItemImpl implements IColorProvidingItem, ICustomItemModel, ICustomCreativeTab {

    public ItemAuraBottle(Item emptyBottle) {
        super("aura_bottle");
        NeoForge.EVENT_BUS.register(new EventHandler());

        DispenserBlock.registerBehavior(emptyBottle, (source, stack) -> {
            Level level = source.level();
            var state = source.state();
            var facing = state.getValue(DispenserBlock.FACING);
            var offset = source.pos().relative(facing);
            var offsetState = level.getBlockState(offset);

            var dispense = stack.split(1);
            if (offsetState.isAir()) {
                var bottle = ItemAuraBottle.create(level, offset);
                if (!bottle.isEmpty())
                    dispense = bottle;
            }
            DefaultDispenseItemBehavior.spawnItem(level, dispense, 6, facing, DispenserBlock.getDispensePosition(source));
            return stack;
        });
    }

    @Override
    public List<ItemStack> getCreativeTabItems() {
        return NaturesAuraAPI.AURA_TYPES.values().stream().map(t -> ItemAuraBottle.setType(new ItemStack(this), t)).toList();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(stack.getDescriptionId() + "." + ItemAuraBottle.getType(stack).getName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColor() {
        return (stack, tintIndex) -> tintIndex > 0 ? FastColor.ARGB32.opaque(ItemAuraBottle.getType(stack).getColor()) : 0xFFFFFFFF;
    }

    @Override
    public void generateCustomItemModel(ItemModelGenerator generator) {
        generator.withExistingParent(this.getBaseName(), "item/generated")
            .texture("layer0", "item/" + this.getBaseName())
            .texture("layer1", "item/" + this.getBaseName() + "_overlay");
    }

    public static IAuraType getType(ItemStack stack) {
        if (!stack.has(Data.TYPE))
            return NaturesAuraAPI.TYPE_OTHER;
        var type = stack.get(Data.TYPE).auraType;
        if (type.isEmpty())
            return NaturesAuraAPI.TYPE_OTHER;
        return NaturesAuraAPI.AURA_TYPES.get(ResourceLocation.parse(type));
    }

    public static ItemStack setType(ItemStack stack, IAuraType type) {
        stack.set(Data.TYPE, new Data(type.getName().toString()));
        return stack;
    }

    private static ItemStack create(Level level, BlockPos pos) {
        var aura = IAuraChunk.getAuraInArea(level, pos, 30);
        if (aura <= -100000) {
            return new ItemStack(ModItems.VACUUM_BOTTLE);
        } else if (aura >= 100000) {
            var spot = IAuraChunk.getHighestSpot(level, pos, 30, pos);
            IAuraChunk.getAuraChunk(level, spot).drainAura(spot, 20000);
            return ItemAuraBottle.setType(new ItemStack(ModItems.AURA_BOTTLE), IAuraType.forLevel(level));
        } else {
            return ItemStack.EMPTY;
        }
    }

    private static class EventHandler {

        @SubscribeEvent
        public void onRightClick(PlayerInteractEvent.RightClickItem event) {
            var held = event.getItemStack();
            if (held.isEmpty() || held.getItem() != ModItems.BOTTLE_TWO_THE_REBOTTLING)
                return;
            var player = event.getEntity();
            HitResult ray = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);
            if (ray.getType() == HitResult.Type.BLOCK)
                return;
            var bottle = ItemAuraBottle.create(player.level(), player.blockPosition());
            if (bottle.isEmpty())
                return;

            if (!player.level().isClientSide) {
                held.shrink(1);
                if (!player.addItem(bottle))
                    player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), bottle));

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1F, 1F);
            }

            player.swing(event.getHand());
        }

    }

    public record Data(String auraType) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("aura_type").forGetter(d -> d.auraType)
        ).apply(i, Data::new));
        public static final DataComponentType<Data> TYPE = DataComponentType.<Data>builder().persistent(Data.CODEC).cacheEncoding().build();

    }

}
