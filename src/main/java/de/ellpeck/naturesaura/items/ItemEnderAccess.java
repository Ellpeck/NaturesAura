package de.ellpeck.naturesaura.items;

import com.google.common.base.Strings;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.BlockEnderCrate;
import de.ellpeck.naturesaura.gui.ContainerEnderCrate;
import de.ellpeck.naturesaura.gui.ModContainers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderAccess extends ItemImpl {
    public ItemEnderAccess() {
        super("ender_access");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(Level levelIn, Player playerIn, Hand handIn) {
        if (handIn != Hand.MAIN_HAND)
            return new ActionResult<>(InteractionResult.PASS, playerIn.getHeldItem(handIn));
        ItemStack stack = playerIn.getHeldItemMainhand();
        String name = BlockEnderCrate.getEnderName(stack);
        if (!Strings.isNullOrEmpty(name)) {
            if (!levelIn.isClientSide && NaturesAuraAPI.instance().extractAuraFromPlayer(playerIn, 10000, false)) {
                NetworkHooks.openGui((ServerPlayer) playerIn, new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("info." + NaturesAura.MOD_ID + ".ender_access", TextFormatting.ITALIC + name + TextFormatting.RESET);
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int windowId, PlayerInventory inv, Player player) {
                        IItemHandler handler = ILevelData.getOverworldData(inv.player.level).getEnderStorage(name);
                        return new ContainerEnderCrate(ModContainers.ENDER_ACCESS, windowId, player, handler);
                    }
                }, buffer -> buffer.writeString(name));
            }
            return new ActionResult<>(InteractionResult.SUCCESS, stack);
        }
        return new ActionResult<>(InteractionResult.FAIL, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable Level levelIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        BlockEnderCrate.addEnderNameInfo(stack, tooltip);
    }
}
