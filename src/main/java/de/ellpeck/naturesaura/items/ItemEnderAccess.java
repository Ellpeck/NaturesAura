package de.ellpeck.naturesaura.items;

import com.google.common.base.Strings;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.BlockEnderCrate;
import de.ellpeck.naturesaura.gui.ContainerEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderAccess extends ItemImpl {

    public ItemEnderAccess() {
        super("ender_access");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        if (handIn != InteractionHand.MAIN_HAND)
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        ItemStack stack = playerIn.getMainHandItem();
        String name = BlockEnderCrate.getEnderName(stack);
        if (!Strings.isNullOrEmpty(name)) {
            if (!levelIn.isClientSide && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 10000, false)) {
                NetworkHooks.openGui((ServerPlayer) playerIn, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TranslatableComponent("info." + NaturesAura.MOD_ID + ".ender_access", ChatFormatting.ITALIC + name + ChatFormatting.RESET);
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                        IItemHandler handler = ILevelData.getOverworldData(inv.player.level).getEnderStorage(name);
                        return new ContainerEnderCrate(ModContainers.ENDER_ACCESS, windowId, player, handler);
                    }
                }, buffer -> buffer.writeUtf(name));
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        BlockEnderCrate.addEnderNameInfo(stack, tooltip);
    }
}
