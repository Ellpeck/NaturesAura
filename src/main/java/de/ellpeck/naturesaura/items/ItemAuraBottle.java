package de.ellpeck.naturesaura.items;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.data.ItemModelGenerator;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ICustomItemModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.dispenser.DefaultDispenseItemBehavior.doDispense;

public class ItemAuraBottle extends ItemImpl implements IColorProvidingItem, ICustomItemModel {

    public ItemAuraBottle(Item emptyBottle) {
        super("aura_bottle");
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        DispenserBlock.registerDispenseBehavior(emptyBottle, (source, stack) -> {
            Level level = source.getLevel();
            BlockState state = source.getBlockState();
            Direction facing = state.get(DispenserBlock.FACING);
            BlockPos offset = source.getBlockPos().offset(facing);
            BlockState offsetState = level.getBlockState(offset);

            ItemStack dispense = stack.split(1);
            if (offsetState.getBlock().isAir(offsetState, level, offset)) {
                if (IAuraChunk.getAuraInArea(level, offset, 30) >= 100000) {
                    dispense = setType(new ItemStack(ItemAuraBottle.this), IAuraType.forLevel(level));

                    BlockPos spot = IAuraChunk.getHighestSpot(level, offset, 30, offset);
                    IAuraChunk.getAuraChunk(level, spot).drainAura(spot, 20000);
                }
            }

            doDispense(level, dispense, 6, facing, DispenserBlock.getDispensePosition(source));
            return stack;
        });
    }

    public static IAuraType getType(ItemStack stack) {
        if (!stack.hasTag())
            return NaturesAuraAPI.TYPE_OTHER;
        String type = stack.getTag().getString("stored_type");
        if (type.isEmpty())
            return NaturesAuraAPI.TYPE_OTHER;
        return NaturesAuraAPI.AURA_TYPES.get(new ResourceLocation(type));
    }

    public static ItemStack setType(ItemStack stack, IAuraType type) {
        stack.getOrCreateTag().putString("stored_type", type.getName().toString());
        return stack;
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            for (IAuraType type : NaturesAuraAPI.AURA_TYPES.values()) {
                ItemStack stack = new ItemStack(this);
                setType(stack, type);
                items.add(stack);
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(stack.getTranslationKey() + "." + getType(stack).getName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
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
            ItemStack held = event.getItemStack();
            if (held.isEmpty() || held.getItem() != ModItems.BOTTLE_TWO_THE_REBOTTLING)
                return;
            Player player = event.getPlayer();
            RayTraceResult ray = rayTrace(player.level, player, RayTraceContext.FluidMode.NONE);
            if (ray.getType() == RayTraceResult.Type.BLOCK)
                return;
            BlockPos pos = player.getPosition();
            if (IAuraChunk.getAuraInArea(player.level, pos, 30) < 100000)
                return;

            if (!player.level.isClientSide) {
                held.shrink(1);

                player.inventory.addItemStackToInventory(
                        setType(new ItemStack(ItemAuraBottle.this), IAuraType.forLevel(player.level)));

                BlockPos spot = IAuraChunk.getHighestSpot(player.level, pos, 30, pos);
                IAuraChunk.getAuraChunk(player.level, spot).drainAura(spot, 20000);

                player.level.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.PLAYERS, 1F, 1F);
            }
            player.swingArm(event.getHand());
        }

    }
}
